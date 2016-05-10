package models;

import com.vividsolutions.jts.geom.Coordinate;
import commons.gis.GisHelper;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "orders")
public class Order extends BaseEntity{

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @JoinColumn(name = "customer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Column
    private Coordinate deliveryPosition;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private Set<OrderProduct> orderProducts;

    // TODO list of order

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Coordinate getDeliveryPosition() {
        return deliveryPosition;
    }

    public void setDeliveryPosition(Coordinate deliveryPosition) {
        this.deliveryPosition = deliveryPosition;
    }

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }
}
