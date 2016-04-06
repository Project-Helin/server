package models.authentication;

import com.google.inject.Inject;
import dao.UserDao;
import models.User;
import play.data.validation.Constraints;

public class Login {

    @Inject
    private UserDao userDao;

    @Constraints.Required
    public String email;
    @Constraints.Required
    public String password;

    public String validate() {

        User user = null;

        user = userDao.authenticateAndGetUser(email, password);

        if (user == null) {
            return "Wrong user or password";
        } else if (!user.isValidated()) {
            return "Account not validated, please check your email";
        }

        return "logged in successfully";
    }

}