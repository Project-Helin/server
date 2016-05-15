package commons.routeCalculationService;

import ch.helin.messages.commons.AssertUtils;
import ch.helin.messages.dto.Action;
import com.google.inject.Inject;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.linearref.LinearGeometryBuilder;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import commons.gis.GisHelper;
import commons.gis.ZoneHelper;
import dao.ProjectsDao;
import dao.RouteDao;
import models.*;
import org.geolatte.geom.*;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RouteCalculationService {

    @Inject
    private RouteDao routeDao;

    @Inject
    private ProjectsDao projectsDao;

    private static final Logger logger = LoggerFactory.getLogger(RouteCalculationService.class);

    public Route calculateRoute(ch.helin.messages.dto.way.Position customerPosition, Project project) {
        Point pointOnPolygon = projectsDao.findPointOnLoadingZone(project.getId());
        AssertUtils.throwExceptionIfNull(pointOnPolygon, "Point on polygon could not be calculated.");

        return calculateRoute(GisHelper.createPosition(pointOnPolygon), customerPosition, project);
    }

    public Route calculateRoute(ch.helin.messages.dto.way.Position dronePosition,
                                   ch.helin.messages.dto.way.Position customerPosition,
                                   Project project) {

        logger.info("State of calculateRoute dronePosition {}", AssertUtils.throwExceptionIfNull(dronePosition));
        logger.info("State of calculateRoute customerPosition {}", AssertUtils.throwExceptionIfNull(customerPosition));
        logger.info("State of calculateRoute Project {}", AssertUtils.throwExceptionIfNull(project));

        logger.info("Drone position: {}", GisHelper.toWktStringWithoutSrid(GisHelper.createPoint(dronePosition.getLon(), dronePosition.getLat())) );
        logger.info("Customer position: {}", GisHelper.toWktStringWithoutSrid(GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat())) );


        MultiLineString skeletonMultiLine = routeDao.calculateSkeleton(project.getId());
        logger.info("Calculate skeleton {}", GisHelper.toWktStringWithoutSrid(skeletonMultiLine));

        List<LineString> rawGraph = new LinkedList<>();

        org.geolatte.geom.Point dronePoint = GisHelper.createPoint(dronePosition.getLon(), dronePosition.getLat());
        LineString lineStringToDrone = calculateShortestLineToPoint(skeletonMultiLine, dronePoint);
        logger.debug("Drone-to-Skeleton: {}", GisHelper.toWktStringWithoutSrid(lineStringToDrone));

        rawGraph.add(lineStringToDrone);
        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToDrone);
        logger.debug("Skeleton after split: {}", skeletonMultiLine);

        org.geolatte.geom.Point customerPoint = GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat());
        LineString lineStringToCustomer;
        if(ZoneHelper.isCustomerInsideDeliveryZone(project.getZones(), customerPoint)){
            lineStringToCustomer = calculateShortestLineToPoint(skeletonMultiLine, customerPoint);
            rawGraph.add(lineStringToCustomer);
            skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToCustomer);
        } else{

            Optional<Zone> maybeZone = project.getZones().stream().filter(x -> x.getType() == ZoneType.DeliveryZone).findFirst();
            Zone deliveryZone = maybeZone.orElseThrow(()-> new RuntimeException("No delivery zone found!"));

            Point intersectionPoint = getIntersectionPointWithPolygon(deliveryZone.getPolygon(), customerPoint);
            lineStringToCustomer = calculateShortestLineToPoint(skeletonMultiLine, intersectionPoint);
            rawGraph.add(lineStringToCustomer);
            skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToCustomer);
        }

        logger.debug("Customer-to-skeleton: {}", lineStringToCustomer);
        logger.debug("Skeleton after split: {}", skeletonMultiLine);

        for(int i=0; i<skeletonMultiLine.getNumGeometries(); i++){
            rawGraph.add((LineString) skeletonMultiLine.getGeometryN(i));
        }

        List<Position> resultFromDijkstra =
                getResultFromDijkstra(rawGraph, lineStringToDrone.getEndPosition(), lineStringToCustomer.getEndPosition());
        logger.info(resultFromDijkstra.toString());;

        Route route = new Route();
        route.setWayPoints(getWaypointListFromPositions(resultFromDijkstra, route));

        Route returnRoute = new Route();
        returnRoute = calculateHeightForFlightPath(route, project.getZones());


        return route;

    }

    private Route calculateHeightForFlightPath(Route route, Set<Zone> zones) {
        UnoverlappingFlyableZoneList unoverlappingZoneList = new UnoverlappingFlyableZoneList(zones);
        unoverlappingZoneList.debugZoneList();

        return null;
    }

    private Point getIntersectionPointWithPolygon(Polygon deliveryZonePolygon, Point customerPoint) {
        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(customerPoint);
        com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(deliveryZonePolygon);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsPolygon, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.Point intersectionPoint = geometryFactory.createPoint(coordinates[0]);

        return (Point) JTS.from(intersectionPoint, GisHelper.getReferenceSystem());
    }

    private List<org.geolatte.geom.Position> getResultFromDijkstra(List<LineString> allPossiblePath,
                                                                   org.geolatte.geom.Position dronePosition,
                                                                   org.geolatte.geom.Position customerPosition){

        UndirectedGraph<Position, LineString> graph = new SimpleGraph<>(LineString.class);

        for (LineString lineString : allPossiblePath) {
            graph.addVertex(lineString.getStartPosition());
            graph.addVertex(lineString.getEndPosition());
            graph.addEdge(lineString.getStartPosition(), lineString.getEndPosition(), lineString);
        }

        logger.debug("Generated graph for Dijkstra: {}", graph.toString());

        DijkstraShortestPath<Position, LineString> algorithm =
                new DijkstraShortestPath<>(graph, dronePosition, customerPosition);

        ConnectivityInspector<Position, LineString> connectivityInspector =
            new ConnectivityInspector<>(graph);
        logger.info("Is connected: {}", connectivityInspector.isGraphConnected());
        for (Set<Position> positions : connectivityInspector.connectedSets()) {
            logger.info("Connected sets: {}", positions);
        }

        GraphPath<Position, LineString> path = algorithm.getPath();
        if (path == null) {
            throw new RuntimeException("Path not found");
        }

        List<org.geolatte.geom.Position> pathVertexList = Graphs.getPathVertexList(path);
        logger.debug("Path-Vertex list: {}", pathVertexList);
        return pathVertexList;
    }

    public LineString calculateShortestLineToPoint(MultiLineString multiLineString, Point point){
        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(point);
        com.vividsolutions.jts.geom.MultiLineString jtsLineString = (com.vividsolutions.jts.geom.MultiLineString) JTS.to(multiLineString);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsLineString, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString jtsResultlineString = geometryFactory.createLineString(coordinates);

        return (LineString) JTS.from(jtsResultlineString, GisHelper.getReferenceSystem());
    }

    public boolean isLineSplitNeeded(LineString line, MultiLineString path){

        com.vividsolutions.jts.geom.LineString jtsLine = (com.vividsolutions.jts.geom.LineString) JTS.to(line);
        com.vividsolutions.jts.geom.MultiLineString jtsMultiLine = (com.vividsolutions.jts.geom.MultiLineString) JTS.to(path);

        Coordinate[] multiLineCoordinates = jtsMultiLine.getCoordinates();

        boolean endPointOnVertex = Arrays.asList(multiLineCoordinates).contains(jtsLine.getEndPoint().getCoordinate());
        boolean startPointOnVertex = Arrays.asList(multiLineCoordinates).contains(jtsLine.getStartPoint().getCoordinate());

        return (startPointOnVertex) || (endPointOnVertex);

    }

    public MultiLineString splitMultiLineStringOnPoint(MultiLineString  skeleton, Point point){

        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(point);
        com.vividsolutions.jts.geom.MultiLineString jtsMultiLine = (com.vividsolutions.jts.geom.MultiLineString) JTS.to(skeleton);

        LinkedList<com.vividsolutions.jts.geom.LineString> lineStrings = new LinkedList<>();
        for(int i=0; i< jtsMultiLine.getNumGeometries(); i++){

            com.vividsolutions.jts.geom.LineString currentLineSting =
                    (com.vividsolutions.jts.geom.LineString) jtsMultiLine.getGeometryN(i);

            if(jtsPoint.isWithinDistance(currentLineSting, GisHelper.getRoundoffPrecision()) && !isPointOnVertex(jtsPoint, jtsMultiLine)){
                logger.info("SPLIT ON: {} ", GisHelper.toWktStringWithoutSrid(point));
                com.vividsolutions.jts.geom.Point start = currentLineSting.getStartPoint();
                com.vividsolutions.jts.geom.Point end = currentLineSting.getEndPoint();

                lineStrings.add(toLineString(start, jtsPoint));
                lineStrings.add(toLineString(jtsPoint, end));

            } else{
                lineStrings.add(currentLineSting);
            }
        }

        com.vividsolutions.jts.geom.LineString[] lineStringArray = lineStrings.toArray(new com.vividsolutions.jts.geom.LineString[]{});

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.MultiLineString returnMultiLineString = geometryFactory.createMultiLineString(lineStringArray);

        return (MultiLineString) JTS.from(returnMultiLineString, skeleton.getCoordinateReferenceSystem());

    }

    private com.vividsolutions.jts.geom.LineString toLineString(com.vividsolutions.jts.geom.Point start, com.vividsolutions.jts.geom.Point jtsPoint) {
        LinearGeometryBuilder linearGeometryBuilder = new LinearGeometryBuilder(new GeometryFactory());
        linearGeometryBuilder.add(start.getCoordinate());
        linearGeometryBuilder.add(jtsPoint.getCoordinate());
        return (com.vividsolutions.jts.geom.LineString) linearGeometryBuilder.getGeometry();
    }

    private MultiLineString splitMultiLineStringOnLineString(MultiLineString  skeleton, LineString lineString){
        Position startPosition = lineString.getStartPosition();
        Position endPosition = lineString.getEndPosition();

        Point startPoint = GisHelper.createPoint(startPosition.getCoordinate(0), startPosition.getCoordinate(1));
        Point endPoint = GisHelper.createPoint(endPosition.getCoordinate(0), endPosition.getCoordinate(1));

        // we need to run the split function twice, because
        // we don't know if startPoint or endPoint is on the skeleton
        skeleton = splitMultiLineStringOnPoint(skeleton, startPoint);
        return splitMultiLineStringOnPoint(skeleton, endPoint);
    }

    private boolean isPointOnVertex(com.vividsolutions.jts.geom.Point jtsPoint, com.vividsolutions.jts.geom.MultiLineString jtsMultiLineString){
        Coordinate[] coordinates = jtsMultiLineString.getCoordinates();
        return Arrays.asList(coordinates).contains(jtsPoint.getCoordinate());
    }


    public List<WayPoint> getWaypointListFromPositions(List<Position> positionList, Route route){
        List<WayPoint> returnWaypointList = new LinkedList<>();

        for (org.geolatte.geom.Position position : positionList) {
            org.geolatte.geom.Position p  = position;
            double lon = p.getCoordinate(0); // <<-- this 0 sucks, but is the x component
            double lat = p.getCoordinate(1);

            WayPoint waypoint = new WayPoint();
            waypoint.setPosition(GisHelper.createPoint(lon, lat));
            waypoint.setAction(Action.FLY);
            waypoint.setOrderNumber(returnWaypointList.size());
            waypoint.setRoute(route);
            returnWaypointList.add(waypoint);
        }

        return returnWaypointList;
    }




}

