package models;


import javax.persistence.*;
import java.util.List;

@Entity(name = "routes")
public class Route extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mission_id")
    private Mission mission;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    @OrderBy("order_number")
    private List<WayPoint> wayPoints;

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
