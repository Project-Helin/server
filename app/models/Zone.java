package models;

import org.geolatte.geom.Polygon;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Zone {

    @Id
    private UUID id;

    @Column
    private String name;

    @Column
    private Polygon polygon;

    @Column
    private Integer height;

    @Column
    @Enumerated(EnumType.STRING)
    private ZoneType type;

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon geom) {
        this.polygon = geom;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType zoneType) {
        this.type = zoneType;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zone zone = (Zone) o;

        return id != null ? id.equals(zone.id) : zone.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}