package models;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Organisation {

    @Id
//    @GeneratedValue(strategy=GenerationType.AUTO)
	private UUID id;

    @Column(name = "name")
    private String name;

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
}
