package models;

import com.vividsolutions.jts.geom.Coordinate;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "drones")
public class Drone extends BaseEntity {

    private String name;
    private UUID token;

    @Column(name = "last_known_position")
    private Coordinate lastKnownPosition;

    private int payload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public Coordinate getLastKnownPosition() {
        return lastKnownPosition;
    }

    public void setLastKnownPosition(Coordinate lastKnownPosition) {
        this.lastKnownPosition = lastKnownPosition;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
