package models;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity(name = "customers")
public class Customer extends BaseEntity {

    @Column
    private String displayName;

    @Column
    private String email;

    @Column
    private String token;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}