package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.geolatte.geom.Polygon;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    @JsonBackReference
    private Polygon geom;

    @Column(name = "height")
    private String height;

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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String wktStringHack(){
        return geom.toString().replace("SRID=4326;", "");
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id=" + id +
                ", geom=" + geom +
                ", height='" + height + '\'' +
                '}';
    }
}