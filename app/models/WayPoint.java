package models;

import javax.persistence.*;

@Entity(name = "way_points")
public class WayPoint extends BaseEntity{

    private int orderNumber;

    @ManyToOne()
    @JoinColumn(name = "route_id")
    private Route route;

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int order_number) {
        this.orderNumber = order_number;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
