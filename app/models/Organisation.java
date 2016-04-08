package models;

import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.Constraint;
import java.util.UUID;

@Entity
public class Organisation {

    @Id
	private UUID id;

    @Column(name = "name")
    @Constraints.Required(message = "error.required")
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
