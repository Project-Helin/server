package models;

import org.geolatte.geom.Point;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private UUID id;

    @Column()
    private String name;

    @Column()
    private Point headquarterPosition;

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
}
