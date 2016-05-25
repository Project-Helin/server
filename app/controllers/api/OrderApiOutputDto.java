package controllers.api;

import ch.helin.messages.dto.MissionDto;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.RouteDto;

import java.util.List;

public class OrderApiOutputDto {
    private String orderId;
    private RouteDto route;
    private Position deliveryPosition;

    private List<MissionDto> missions;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public RouteDto getRoute() {
        return route;
    }

    public void setRoute(RouteDto route) {
        this.route = route;
    }

    public Position getDeliveryPosition() {
        return deliveryPosition;
    }

    public void setDeliveryPosition(Position dropPosition) {
        this.deliveryPosition = dropPosition;
    }

    public List<MissionDto> getMissions() {
        return missions;
    }

    public void setMissions(List<MissionDto> missions) {
        this.missions = missions;
    }
}
