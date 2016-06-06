package models;

import org.geolatte.geom.Polygon;

import javax.persistence.*;

@Entity(name = "zones")
public class Zone extends BaseEntity{

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
    public String toString() {
        return "Zone{" +
                "name='" + name + '\'' +
                ", polygon=" + polygon +
                ", height=" + height +
                ", type=" + type +
                ", project=" + project +
                '}';
    }
}