package models;

import models.utils.AuthenticationHelper;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Constraint;
import java.util.UUID;

@Entity(name="Users")
public class User {

    @Id
	private UUID id;

    @Constraints.Required
    @Column
    private String name;

    @Constraints.Required
    @Column
    private String email;

    @Constraints.Required
    @Column
    private String password;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @Column
    private boolean validated = false;

//    @ManyToMany
//    private List<Organisation> organisations;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            this.password = AuthenticationHelper.createPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
