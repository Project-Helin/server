package models;

import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "products")
public class Product extends BaseEntity{

    @Column
    @Constraints.Required
    private String name;

    @Column
    @Constraints.Required
    private Double price;

    @Column(name = "weight_gramm")
    @Constraints.Required
    private Integer weightGramm;

    @JoinColumn(name = "organisation_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

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

    public Integer getWeightGramm() {
        return weightGramm;
    }

    public void setWeightGramm(Integer weightGramm) {
        this.weightGramm = weightGramm;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

}