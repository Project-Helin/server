package models;

import models.utils.AuthenticationHelper;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "users")
public class User extends BaseEntity {

    @Constraints.Required
    @Constraints.Email
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


    @ManyToMany(mappedBy="administrators")
    private Set<Organisation> organisations;

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
            if (password != null && !password.isEmpty()) {
                this.password = AuthenticationHelper.createPassword(password);
            }
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

    public Set<Organisation> getOrganisations() {
        if (organisations == null) {
            organisations = new HashSet<>();
        }
        return organisations;
    }

    public void setOrganisations(Set<Organisation> organisations) {
        this.organisations = organisations;
    }

}
