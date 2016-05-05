package models;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "missions")
public class Mission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private MissionState state;

    @OneToMany(mappedBy = "mission")
    private Set<DroneInfo> droneInfos;

    @OneToOne(mappedBy = "currentMission")
    private Drone drone;

    @OneToOne(mappedBy = "mission")
    private Route route;

    public MissionState getState() {
        return state;
    }

    public void setState(MissionState state) {
        this.state = state;
    }

    public Set<DroneInfo> getDroneInfos() {
        return droneInfos;
    }

    public void setDroneInfos(Set<DroneInfo> droneInfos) {
        this.droneInfos = droneInfos;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
