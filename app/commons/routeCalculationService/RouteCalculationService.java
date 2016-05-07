package commons.routeCalculationService;

import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import com.google.inject.Inject;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.gis.GisHelper;
import dao.RouteDao;
import javafx.geometry.Pos;
import models.Project;
import org.apache.commons.lang3.RandomUtils;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;
import org.geolatte.geom.Position;
import org.geolatte.geom.jts.JTS;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RouteCalculationService {

    @Inject
    private RouteDao routeDao;

    private static final Logger logger = LoggerFactory.getLogger(RouteCalculationService.class);


    public RouteDto calculateRoute(ch.helin.messages.dto.way.Position dronePosition,
                                   ch.helin.messages.dto.way.Position customerPosition,
                                   Project project) {

        logger.info("State of calculateRoute dronePosition {}", dronePosition);
        logger.info("State of calculateRoute customerPosition {}", customerPosition);
        logger.info("State of calculateRoute Project {}", project);

        //List<LineString> skeletonLineStrings = routeDao.calculateSkeleton(project.getId());

        MultiLineString skeletonMultiLine = routeDao.calculateSkeleton(project.getId());
        System.out.println("Skeleton: " + GisHelper.toWktStringWithoutSrid(skeletonMultiLine));
        List<LineString> rawGraph = new LinkedList<>();

        org.geolatte.geom.Point dronePoint = GisHelper.createPoint(dronePosition.getLon(), dronePosition.getLat());
        LineString lineStringToDrone = calculateShortestLineToPoint(skeletonMultiLine, dronePoint);
        System.out.println("LineToDrone: " + GisHelper.toWktStringWithoutSrid(lineStringToDrone));
        rawGraph.add(lineStringToDrone);
        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToDrone);
        System.out.println("SkeletonAfterSplit: " + GisHelper.toWktStringWithoutSrid(skeletonMultiLine));


        org.geolatte.geom.Point customerPoint = GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat());
        LineString lineStringToCustomer = calculateShortestLineToPoint(skeletonMultiLine, customerPoint);
        System.out.println("lineStringToCustomer: " + GisHelper.toWktStringWithoutSrid(lineStringToCustomer));

        rawGraph.add(lineStringToCustomer);
        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToCustomer);
        System.out.println("SkeletonAfterSplit: " + GisHelper.toWktStringWithoutSrid(skeletonMultiLine));


        System.out.println(skeletonMultiLine.getNumGeometries());
        for(int i=0; i<skeletonMultiLine.getNumGeometries(); i++){
            rawGraph.add((LineString) skeletonMultiLine.getGeometryN(i));
        }
        System.out.println(rawGraph.toString());

        RouteDto route = new RouteDto();

        List<Position> resultFromDijkstra =
                getResultFromDijkstra(rawGraph, lineStringToDrone.getEndPosition(), lineStringToCustomer.getEndPosition());
        System.out.println("Result from dijkstra: " + resultFromDijkstra.size());

        for (Position position : resultFromDijkstra) {
            Position p  = position;
            double lon = p.getCoordinate(0); // <<-- this 0 sucks, but is the x component
            double lat = p.getCoordinate(1);

            Waypoint waypoint = new Waypoint();
            waypoint.setId(UUID.randomUUID());
            waypoint.setPosition(new ch.helin.messages.dto.way.Position(lat, lon, RandomUtils.nextInt(0, 100)));
            route.getWayPoints().add(waypoint);
        }

        return route;

        /*

        calculateShortestLineToPoint(skeletonLineStrings, dronePosition)


        List<LineString> listLineString = routeDao.calculateSkeleton(project.getId());
        List<LineString> rawGraph = new LinkedList<>();
        MultiLineString addaptedMultiLineString = new MultiLineString();

        LineString[] lineStrings1 = listLineString.toArray(new LineString[]{});
        MultiLineString lineStrings = new MultiLineString(lineStrings1);


        org.geolatte.geom.Point dronePoint = GisHelper.createPoint(dronePosition.getLon(), dronePosition.getLat());
        LineString e = calculateShortestLineToPoint(lineStrings, dronePoint);

        Point point = GisHelper.createPoint(e.getStartPosition().getCoordinate(0), e.getStartPosition().getCoordinate(1));
        addaptedMultiLineString = splitMultiLineStringOnPoint(lineStrings, point);
        System.out.println("---");
        System.out.println(addaptedMultiLineString);


        point = GisHelper.createPoint(e.getEndPosition().getCoordinate(0), e.getEndPosition().getCoordinate(1));
        addaptedMultiLineString = splitMultiLineStringOnPoint(addaptedMultiLineString, point);
        System.out.println("---");
        System.out.println(addaptedMultiLineString);


        rawGraph.add(e);


        org.geolatte.geom.Point customerPoint = GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat());
        LineString b = calculateShortestLineToPoint(lineStrings, customerPoint);

        point = GisHelper.createPoint(b.getStartPosition().getCoordinate(0), b.getStartPosition().getCoordinate(1));
        addaptedMultiLineString = splitMultiLineStringOnPoint(addaptedMultiLineString, point);
        System.out.println("---");
        System.out.println(addaptedMultiLineString);


        point = GisHelper.createPoint(b.getEndPosition().getCoordinate(0), b.getEndPosition().getCoordinate(1));
        addaptedMultiLineString = splitMultiLineStringOnPoint(addaptedMultiLineString, point);
        System.out.println("---");
        System.out.println(addaptedMultiLineString);


        rawGraph.add(b);

        Iterator iterator = addaptedMultiLineString.iterator();
        while(iterator.hasNext()){
            rawGraph.add ((LineString) iterator.next());
        }


        System.out.println(rawGraph);

        RouteDto route = new RouteDto();

        List<Position> resultFromDijkstra =
                getResultFromDijkstra(rawGraph, e.getEndPosition(), b.getEndPosition());

        for (Position position : resultFromDijkstra) {
            Position p  = position;
            double lon = p.getCoordinate(0); // <<-- this 0 sucks, but is the x component
            double lat = p.getCoordinate(1);

            Waypoint waypoint = new Waypoint();
            waypoint.setId(UUID.randomUUID());
            waypoint.setPosition(new ch.helin.messages.dto.way.Position(lat, lon, RandomUtils.nextInt(0, 100)));
            route.getWayPoints().add(waypoint);
        }
*/

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

        System.out.println(graph.toString());

        DijkstraShortestPath<Position, LineString> algorithm =
                new DijkstraShortestPath<>(graph, dronePosition, customerPosition);

        GraphPath<Position, LineString> path = algorithm.getPath();

        List<org.geolatte.geom.Position> pathVertexList = Graphs.getPathVertexList(path);

        System.out.println(pathVertexList);
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

    public MultiLineString splitMultiLineStringOnPoint(MultiLineString  path, Point point){

        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(point);
        com.vividsolutions.jts.geom.MultiLineString jtsMultiLine = (com.vividsolutions.jts.geom.MultiLineString) JTS.to(path);

        LinkedList<com.vividsolutions.jts.geom.LineString> lineStrings = new LinkedList<>();
        for(int i=0; i< jtsMultiLine.getNumGeometries(); i++){

            com.vividsolutions.jts.geom.LineString currentLineSting =
                    (com.vividsolutions.jts.geom.LineString) jtsMultiLine.getGeometryN(i);


            System.out.println("Distance: " + jtsPoint.distance(currentLineSting));


            //jtsPoint.intersects(currentLineString)
            double MIN_DISTANCE = 0.00000000001; // below 1mm - so it should be equal
            if(jtsPoint.isWithinDistance(currentLineSting, 0.00000000001) && !isPointOnVertex(jtsPoint, jtsMultiLine)){
                System.out.println("SPLIT ON: " + GisHelper.toWktStringWithoutSrid(point));
                // Split current line if point is on and not on point
                LocationIndexedLine locationIndexLine = new LocationIndexedLine(currentLineSting);
                LinearLocation locationOnLine = locationIndexLine.indexOf(jtsPoint.getCoordinate());

                com.vividsolutions.jts.geom.LineString lineStringToPoint =
                        (com.vividsolutions.jts.geom.LineString) locationIndexLine.extractLine(locationIndexLine.getStartIndex(), locationOnLine);
                com.vividsolutions.jts.geom.LineString lineStringFromPoint =
                        (com.vividsolutions.jts.geom.LineString) locationIndexLine.extractLine(locationOnLine, locationIndexLine.getEndIndex());

                lineStrings.add(lineStringToPoint);
                lineStrings.add(lineStringFromPoint);

            } else{
                lineStrings.add(currentLineSting);
            }
        }

        com.vividsolutions.jts.geom.LineString[] lineStringArray = lineStrings.toArray(new com.vividsolutions.jts.geom.LineString[]{});

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.MultiLineString returnMultiLineString = geometryFactory.createMultiLineString(lineStringArray);

        return (MultiLineString) JTS.from(returnMultiLineString, path.getCoordinateReferenceSystem());

    }

    private MultiLineString
    splitMultiLineStringOnLineString(MultiLineString  path, LineString lineString){
        Position startPosition = lineString.getStartPosition();
        Position endPosition = lineString.getEndPosition();

        Point startPoint = GisHelper.createPoint(startPosition.getCoordinate(0), startPosition.getCoordinate(1));
        Point endPoint = GisHelper.createPoint(endPosition.getCoordinate(0), endPosition.getCoordinate(1));

        path = splitMultiLineStringOnPoint(path, startPoint);
        return splitMultiLineStringOnPoint(path, endPoint);
    }

    private boolean isPointOnVertex(com.vividsolutions.jts.geom.Point jtsPoint, com.vividsolutions.jts.geom.MultiLineString jtsMultiLineString){
        Coordinate[] coordinates = jtsMultiLineString.getCoordinates();
        return Arrays.asList(coordinates).contains(jtsPoint.getCoordinate());
    }



}

