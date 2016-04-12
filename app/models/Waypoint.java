package models;

import ch.helin.messages.dto.Action;
import com.vividsolutions.jts.geom.Coordinate;

public abstract class Waypoint {

    private Coordinate position;
    private Action action;

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
