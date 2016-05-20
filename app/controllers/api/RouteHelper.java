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

    public static Route positionListToRoute(List<Position> positionList) {
        Route route = new Route();

        List<WayPoint> flightToDeliveryList = new ArrayList<>();
        for (Position position : positionList) {
            WayPoint waypoint = new WayPoint();
            waypoint.setHeight(position.getHeight());
            waypoint.setPosition(GisHelper.createPoint(position.getLon(), position.getLat()));
            waypoint.setAction(Action.FLY);
            waypoint.setRoute(route);
            flightToDeliveryList.add(waypoint);
        }

        // Last point is for potential drop off
        flightToDeliveryList.get(flightToDeliveryList.size()-1).setAction(Action.DROP);

        List<WayPoint> flightToHomeList = new ArrayList(Lists.reverse(flightToDeliveryList));

        // Remove Dropoff, it is already done - next coordinate is the 'second last'
        flightToHomeList.remove(0);

        List<WayPoint> completeFlightList = new ArrayList<>();
        completeFlightList.addAll(flightToDeliveryList);
        completeFlightList.addAll(flightToHomeList);

        int order = 0;
        for (WayPoint wayPoint : completeFlightList) {
            wayPoint.setOrderNumber(order++);
        }

        route.setWayPoints(completeFlightList);
        return route;
    }
}
