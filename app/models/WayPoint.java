package models;

import ch.helin.messages.dto.Action;
import org.geolatte.geom.Point;

import javax.persistence.*;

@Entity(name = "way_points")
public class WayPoint extends BaseEntity{
    private int orderNumber;
    private Point position;

    @Transient
    private int height;

    @Enumerated(EnumType.STRING)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "WayPoint{" +
                "orderNumber=" + orderNumber +
                ", position=" + position +
                ", height=" + height +
                ", action=" + action +
                ", route=" + route +
                '}';
    }
}
