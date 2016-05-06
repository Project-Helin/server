package commons.routeCalculationService;

import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import com.google.inject.Inject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.gis.GisHelper;
import dao.RouteDao;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RouteCalculationService {

    @Inject
    private RouteDao routeDao;

    private static final Logger logger = LoggerFactory.getLogger(RouteCalculationService.class);


    public RouteDto calculateRoute(ch.helin.messages.dto.way.Position dronePosition,
                                   ch.helin.messages.dto.way.Position customerPosition,
                                   Project project) {

        logger.info("State of calculateRoute dronePosition {}", dronePosition);
        logger.info("State of calculateRoute customerPosition {}", customerPosition.toString());
        logger.info("State of calculateRoute Project {}", project);

        List<LineString> listLineString = routeDao.calculateSkeleton(project.getId());

        LineString[] lineStrings1 = listLineString.toArray(new LineString[]{});
        MultiLineString<Position> lineStrings = new MultiLineString<>(lineStrings1);

        org.geolatte.geom.Point dronePoint = GisHelper.createPoint(dronePosition.getLon(), dronePosition.getLat());

        LineString e = routeDao.calculateShortestLineToPoint(lineStrings, dronePoint);
        listLineString.add(e);



        org.geolatte.geom.Point customerPoint = GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat());
        LineString b = routeDao.calculateShortestLineToPoint(lineStrings, customerPoint);
        listLineString.add(b);

        RouteDto route = new RouteDto();

        List<Position> resultFromDijkstra =
                getResultFromDijkstra(listLineString, e.getEndPosition(), b.getEndPosition());

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

    }



    private List<org.geolatte.geom.Position> getResultFromDijkstra(List<LineString> allPossiblePath, org.geolatte.geom.Position dronePosition, org.geolatte.geom.Position customerPosition){
        UndirectedGraph<Position, LineString> graph = new SimpleGraph<>(LineString.class);

        for (LineString lineString : allPossiblePath) {
            graph.addVertex(lineString.getStartPosition());
            graph.addVertex(lineString.getEndPosition());
            graph.addEdge(lineString.getStartPosition(), lineString.getEndPosition(), lineString);
        }

        DijkstraShortestPath<Position, LineString> algorithm =
                new DijkstraShortestPath<>(graph, dronePosition, customerPosition);

        GraphPath<Position, LineString> path = algorithm.getPath();


        List<org.geolatte.geom.Position> pathVertexList = Graphs.getPathVertexList(path);

        System.out.println(pathVertexList);
        return pathVertexList;
    }



}
