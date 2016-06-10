package mappers;

import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import service.gis.GisHelper;
import models.Route;
import models.WayPoint;

import java.util.List;
import java.util.stream.Collectors;

public class RouteMapper {

    public RouteDto convertToRouteDto (Route route) {

        RouteDto routeDto = new RouteDto();

        if (route != null && route.getWayPoints() != null) {
            List<Waypoint> wayPointDtos = route.getWayPoints().stream()
                    .map(this::convertToWaypointDto)
                    .collect(Collectors.toList());

            routeDto.setWayPoints(wayPointDtos);
        }

        return routeDto;
    }

    public Waypoint convertToWaypointDto (WayPoint wayPoint) {
        Waypoint wayPointDto = new Waypoint();

        wayPointDto.setId(wayPoint.getId());
        Position position = GisHelper.createPosition(wayPoint.getPosition());

        if (wayPoint.getHeight() != null) {
            position.setHeight(wayPoint.getHeight());
        }

        wayPointDto.setPosition(position);
        wayPointDto.setId(wayPoint.getId());
        wayPointDto.setAction(wayPoint.getAction());

        return wayPointDto;
    }

}
