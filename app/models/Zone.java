package models;

import org.geolatte.geom.Polygon;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Zone {

    @Id
    private UUID id;

    @Column
    private Polygon geom;

    @Column(name = "height")
    private Integer height;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ZoneType zoneType;

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Polygon getGeom() {
        return geom;
    }

    public void setGeom(Polygon geom) {
        this.geom = geom;
    }

    /*
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
    */

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}