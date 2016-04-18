package models;

import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class Organisation extends BaseEntity {

    @Column(name = "name")
    @Constraints.Required(message = "Name cannot be empty")
    private String name;

    private String token;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="organisations_users",
            joinColumns=@JoinColumn(name="organisation_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="id"))

    private Set<User> administrators;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getAdministrators() {
        if (administrators == null) {
            administrators = new HashSet<>();
        }
        return administrators;
    }

    public void setAdministrators(Set<User> administrators) {
        this.administrators = administrators;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
