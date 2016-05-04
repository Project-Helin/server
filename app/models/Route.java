package models;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Set;

@Entity(name = "routes")
public class Route extends BaseEntity {

    @OneToOne()
    @JoinColumn(name="mission_id")
    private Mission mission;

    @OneToMany(mappedBy = "route")
    private Set<WayPoint> wayPoints;

    public Set<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(Set<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
