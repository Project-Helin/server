package models;

import commons.GisHelper;
import org.geolatte.geom.Point;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Project {

    @Id
	private UUID id;

    @Column()
    private String name;

    @Column()
    private Point headquarterPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
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

    public Point getHeadquarterPosition() {
        return headquarterPosition;
    }

    public void setHeadquarterPosition(Point headquarterPosition) {
        this.headquarterPosition = headquarterPosition;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public String headquarterPosition(){
        return GisHelper.toWktStringWithoutSrid(headquarterPosition);
    }
}
