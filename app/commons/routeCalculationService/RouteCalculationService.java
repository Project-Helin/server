package commons.routeCalculationService;

import ch.helin.messages.commons.AssertUtils;
import com.google.inject.Inject;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.linearref.LinearGeometryBuilder;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.gis.GisHelper;
import commons.gis.GraphHelper;
import commons.gis.ShortestLineFactory;
import commons.gis.ZoneHelper;
import dao.ProjectsDao;
import dao.RouteDao;
import models.*;
import org.geolatte.geom.*;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;

import org.geolatte.geom.Point;
import org.geolatte.geom.jts.JTS;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.Pseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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

        ShortestLineFactory slFactory = new ShortestLineFactory();
        org.geolatte.geom.Point dronePoint = GisHelper.createPoint(dronePosition.getLon(), dronePosition.getLat());
        LineString lineStringToDrone = slFactory.calculateShortestLineToPoint(skeletonMultiLine, dronePoint);
        logger.debug("Drone-to-Skeleton: {}", GisHelper.toWktStringWithoutSrid(lineStringToDrone));

        rawGraph.add(lineStringToDrone);
        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToDrone);
        logger.debug("Skeleton after split: {}", skeletonMultiLine);

        org.geolatte.geom.Point customerPoint = GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat());
        LineString lineStringToCustomer;
        if(ZoneHelper.isCustomerInsideDeliveryZone(project.getZones(), customerPoint)){
            lineStringToCustomer = slFactory.calculateShortestLineToPoint(skeletonMultiLine, customerPoint);
            rawGraph.add(lineStringToCustomer);
            skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToCustomer);
        } else{

            List<com.vividsolutions.jts.geom.Polygon> polygonList = project.
                    getZones().stream()
                    .filter(x -> x.getType() == ZoneType.DeliveryZone)
                    .map(x -> ZoneHelper.convertZoneToJtsPolygon(x))
                    .collect(Collectors.toList());

            GeometryFactory polygonFactory = new GeometryFactory();
            MultiPolygon deliveryZonePolygons = polygonFactory.createMultiPolygon(polygonList.toArray(new com.vividsolutions.jts.geom.Polygon[]{}));
            org.geolatte.geom.MultiPolygon zoneMultiPolygon = (org.geolatte.geom.MultiPolygon) JTS.from(deliveryZonePolygons, GisHelper.getReferenceSystem());

            Point intersectionPoint = getIntersectionPointWithPolygon(zoneMultiPolygon, customerPoint);
            lineStringToCustomer = slFactory.calculateShortestLineToPoint(skeletonMultiLine, intersectionPoint);
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

        LineString lineStringFromPositions = getLineStringFromPositions(resultFromDijkstra);

        Route route = new Route();
        List<WayPoint> wayPoints = calculateHeightForFlightPath(route, project.getZones(), lineStringFromPositions);
        route.setWayPoints(wayPoints);

        return route;

    }

    private List<WayPoint> calculateHeightForFlightPath(Route route, Set<Zone> zones, LineString lineString) {
        UnoverlappingFlyableZoneList unoverlappingZoneList = new UnoverlappingFlyableZoneList(zones);
        unoverlappingZoneList.debugZoneList();
        LineString lineString1 = unoverlappingZoneList.cutLineStringOnPolygonBorder(lineString);

        List<Position> positionList = new ArrayList<>();

        PositionSequence positions = lineString1.getPositions();
        for(int i = 0; i<positions.size(); i++){
            positionList.add(positions.getPositionN(i));
        }

        List<WayPoint> wayPoints = unoverlappingZoneList.assignHeightForPositions(positionList);

        logger.debug("List waypoints with Height {}", wayPoints.toString());

        return wayPoints;
    }

    private org.geolatte.geom.Point getIntersectionPointWithPolygon(org.geolatte.geom.MultiPolygon deliveryZonePolygon, org.geolatte.geom.Point customerPoint) {
        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(customerPoint);
        com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(deliveryZonePolygon);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsPolygon, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.Point intersectionPoint = geometryFactory.createPoint(coordinates[0]);

        return (org.geolatte.geom.Point) JTS.from(intersectionPoint, GisHelper.getReferenceSystem());
    }

    private List<org.geolatte.geom.Position> getResultFromDijkstra(List<LineString> allPossiblePath,
                                                                   org.geolatte.geom.Position dronePosition,
                                                                   org.geolatte.geom.Position customerPosition){

        Pseudograph<Position, LineString> graph = new Pseudograph<>(LineString.class);

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
                System.out.println("SPLIT ON: " + GisHelper.toWktStringWithoutSrid(point));
                // Split current line if point is on and not on point
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


    public LineString getLineStringFromPositions(List<Position> positionList){
        List<Coordinate> coordinateList = new ArrayList<>();

        for (org.geolatte.geom.Position position : positionList) {
            org.geolatte.geom.Position p  = position;
            double lon = p.getCoordinate(0); // <<-- this 0 sucks, but is the x component
            double lat = p.getCoordinate(1);

            Coordinate coordinate = new Coordinate(lon, lat);
            coordinateList.add(new Coordinate(lon, lat));
        }

        GeometryFactory gf = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString lineString = gf.createLineString(coordinateList.toArray(new Coordinate[]{}));
        return (LineString) JTS.from(lineString, GisHelper.getReferenceSystem());
    }




}

