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
        List<WayPoint> flightToDeliveryList = new ArrayList<>();
        for (Position position : positionList) {
            WayPoint waypoint = new WayPoint();
            waypoint.setHeight(position.getHeight());
            waypoint.setPosition(GisHelper.createPoint(position.getLon(), position.getLat()));
            waypoint.setAction(Action.FLY);

            flightToDeliveryList.add(waypoint);
        }

        // Last point is for potential drop off
        flightToDeliveryList.get(flightToDeliveryList.size()-1).setAction(Action.DROP);

        List<WayPoint> flightToHomeList = new ArrayList(Lists.reverse(flightToDeliveryList));

        // Remove Dropoff, it is already done - next coordinate is the 'second last'
        flightToHomeList.remove(0); //.setAction(Action.DROP);

        List<WayPoint> completeFlightList = new ArrayList<>();
        completeFlightList.addAll(flightToDeliveryList);
        completeFlightList.addAll(flightToHomeList);

        Route route = new Route();

        route.setWayPoints(completeFlightList);
        return route;
    }
}
