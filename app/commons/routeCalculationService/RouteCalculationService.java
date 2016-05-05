package commons.routeCalculationService;

import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import com.vividsolutions.jts.geom.Coordinate;
import models.Zone;

import java.util.List;

public class RouteCalculationService {

    public RouteDto calculateRoute(List<Waypoint> waypoints, Waypoint startPoint, List<Zone> zones){

        Zone zone = zones.get(0);

        List<Coordinate> coordinates = PolygonManipulationHelper.degeneratePolygonToPointList(zone.getPolygon());


        //coordinates.add()
        System.out.println(coordinates);

        return new RouteDto();
    }






}
