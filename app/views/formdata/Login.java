package views.formdata;

import play.data.validation.Constraints;

public class Login {

    @Constraints.Required
    @Constraints.Email
    private String email;

    @Constraints.Required
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}