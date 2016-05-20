package controllers.api;

import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.RouteDto;

public class OrderApiOutputDto {

    private String orderId;
    private RouteDto route;
    private Position deliveryPosition;


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
}
