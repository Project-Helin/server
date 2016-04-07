package models;

import org.geolatte.geom.Polygon;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "products")
public class Product {

    @Id
    private UUID id;

    @Column
    private String name;

    @Column
    private Double price;

    @Column(name = "weight_gramm")
    private Integer wightGramm;

    @JoinColumn(name = "organisation_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getWightGramm() {
        return wightGramm;
    }

    public void setWightGramm(Integer wightGramm) {
        this.wightGramm = wightGramm;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product zone = (Product) o;

        return id != null ? id.equals(zone.id) : zone.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}