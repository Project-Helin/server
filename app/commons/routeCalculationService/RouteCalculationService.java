package commons.routeCalculationService;

import ch.helin.messages.dto.way.Route;
import ch.helin.messages.dto.way.Waypoint;
import com.vividsolutions.jts.geom.Coordinate;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import commons.dijkstra.Dijkstra;
import models.Zone;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;

import java.util.Arrays;
import java.util.List;

public class RouteCalculationService {

    public Route calculateRoute(List<Waypoint> waypoints, Waypoint startPoint, List<Zone> zones){

        Zone zone = zones.get(0);

        List<Coordinate> coordinates = PolygonManipulationHelper.degeneratePolygonToPointList(zone.getPolygon());


        //coordinates.add()
        System.out.println(coordinates);

        return new Route();
    }






}
