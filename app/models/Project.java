package models;

import javax.persistence.*;
import java.util.*;

@Entity(name = "projects")
public class Project extends BaseEntity {

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", orphanRemoval = true)
    private Set<Zone> zones = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "projects_products",
        joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Drone> drones;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setZones(Set<Zone> zones) {
        this.zones = zones;
        for (Zone each : zones) {
            each.setProject(this);
        }
    }

    public void setZones(Zone... zones) {
        this.zones = new HashSet<>();
        for (Zone each : zones) {
            each.setProject(this);
            this.zones.add(each);
        }
    }

    public Set<Zone> getZones() {
        return zones;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public Set<Drone> getDrones() {
        return drones;
    }

    public void setDrones(Set<Drone> drones) {
        this.drones = drones;
    }
}
