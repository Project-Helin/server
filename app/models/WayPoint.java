package models;

import ch.helin.messages.dto.Action;
import org.geolatte.geom.Point;

import javax.persistence.*;

@Entity(name = "way_points")
public class WayPoint extends BaseEntity{

    private int orderNumber;
    private Point position;

    private int height;

    //fix point is needed to indicate if point has special purpose,
    // like climbing or start stop, so it can not be removed or rearranged
    private boolean fixpoint;

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

    public boolean isFixpoint() {
        return fixpoint;
    }

    public void setFixpoint(boolean fixpoint) {
        this.fixpoint = fixpoint;
    }
}
