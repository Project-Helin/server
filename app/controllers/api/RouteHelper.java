package controllers.api;

import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.way.Position;
import com.google.common.collect.Lists;
import commons.gis.GisHelper;
import models.Route;
import models.WayPoint;

import java.util.ArrayList;
import java.util.List;


public class RouteHelper {

    public static Route positionListToRoute(List<Position> positions) {
        Route route = new Route();

        List<WayPoint> flightToDelivery = createFlightToDelivery(positions, route);
        List<WayPoint> flightToHome = createFlightToHome(positions, route);

        List<WayPoint> completeFlightList = new ArrayList<>();
        completeFlightList.addAll(flightToDelivery);
        completeFlightList.addAll(flightToHome);

        // set order number correctly
        int order = 0;
        for (WayPoint wayPoint : completeFlightList) {
            wayPoint.setOrderNumber(order++);
        }

        route.setWayPoints(completeFlightList);
        return route;
    }

    private static List<WayPoint> createFlightToDelivery(List<Position> positions, Route route) {
        List<WayPoint> flightToDelivery = new ArrayList<>();

        for (Position position : positions) {
            flightToDelivery.add(createWaypoint(route, position));
        }

        // Last point is for potential drop off
        flightToDelivery.get(flightToDelivery.size() - 1).setAction(Action.DROP);
        return flightToDelivery;
    }

    private static List<WayPoint> createFlightToHome(List<Position> positions, Route route) {
        List<WayPoint> flightToHome = new ArrayList<>();
        for (Position reversePosition : Lists.reverse(positions)){
            flightToHome.add(createWaypoint(route, reversePosition));
        }

        // Remove Dropoff, it is already done - next coordinate is the 'second last'
        flightToHome.remove(0);
        return flightToHome;
    }

    private static WayPoint createWaypoint(Route route, Position position) {
        WayPoint waypoint = new WayPoint();
        waypoint.setHeight(position.getHeight());
        waypoint.setPosition(GisHelper.createPoint(position.getLon(), position.getLat()));
        waypoint.setAction(Action.FLY);
        waypoint.setRoute(route);
        return waypoint;
    }
}
