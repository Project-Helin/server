package models;

import javax.persistence.*;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;

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

    public Order getOrder() {
        return order;
    }

    public Mission setOrder(Order order) {
        this.order = order;
        return this;
    }

    public OrderProduct getOrderProduct() {
        return orderProduct;
    }

    public Mission setOrderProduct(OrderProduct orderProduct) {
        this.orderProduct = orderProduct;
        return this;
    }
}
