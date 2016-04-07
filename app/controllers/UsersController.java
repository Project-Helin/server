package controllers;

import com.google.inject.Inject;
import dao.UserDao;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.Login;
import views.html.login;
import views.html.users.add;

import java.util.UUID;

public class UsersController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Inject
    private UserDao userDao;


    public Result login() {
        Form<Login> form = formFactory
                .form(Login.class);
        return ok(login.render(form));
    }

    @Transactional
    public Result loginPost() {
        Form<Login> form = formFactory
                .form(Login.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            return badRequest(login.render(form));
        } else {
            User user = userDao.authenticateAndGetUser(form.get().getEmail(), form.get().getPassword());
            if (user == null) {
                flash("error", "Wrong user or password");
                return badRequest(login.render(form));
                //TODO add Mailservice and validate Email
//            } else if (!user.isValidated()) {
//                flash("error", "Account not validated, please check your email");
//                return badRequest(login.render(form));
            } else {
                session("email", form.get().getEmail());
                return redirect("/");
            }
        }
    }

    public Result add() {
        Form<User> form = formFactory
                .form(User.class);

        return ok(add.render(form));
    }

    @Transactional
    public Result create() {
        Form<User> form = formFactory
                .form(User.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            return badRequest(add.render(form));
        } else {
            User user = form.get();
            user.setId(UUID.randomUUID());
            user.setConfirmationToken(UUID.randomUUID().toString());
            userDao.persist(user);
            flash("info", "You should have received an E-Mail confirmation, please click on the link in the E-Mail");
            return redirect("/");
        }
    }
}
