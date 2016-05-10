package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "missions")
public class Mission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private MissionState state;

    @OneToMany(mappedBy = "mission")
    @OrderBy("clientTime DESC")
    private List<DroneInfo> droneInfos;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "drone_id")
    private Drone drone;

    @OneToOne(mappedBy = "mission")
    private Route route;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    public MissionState getState() {
        return state;
    }

    public void setState(MissionState state) {
        this.state = state;
    }

    public List<DroneInfo> getDroneInfos() {
        return droneInfos;
    }

    public void setDroneInfos(List<DroneInfo> droneInfos) {
        this.droneInfos = droneInfos;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public Set<OrderProduct> getOrderProducts() {
        //TODO Load real orderProducts assigned to this mission;
        return new HashSet<>();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
