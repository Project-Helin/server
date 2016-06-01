package controllers;

import com.google.inject.Inject;
import commons.SessionHelper;
import dao.UserDao;
import models.Organisation;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;
import views.html.users.add;

import java.util.UUID;

public class UsersController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Inject
    private UserDao userDao;

    @Inject
    private SessionHelper sessionHelper;


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
                form.reject("Wrong user or password");
                return badRequest(login.render(form));
            } else {
                sessionHelper.setUser(user, session());

                if (user.getOrganisations().size() > 0) {
                    Organisation firstOrganisation = user.getOrganisations().iterator().next();
                    sessionHelper.setOrganisation(firstOrganisation, session());
                }

                flash("success", "Welcome " + user.getName());
                return redirect("/");
            }
        }
    }

    public Result logout() {
        session().clear();
        return redirect("/");
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

        } else if(isEmailAddressTaken(form.get().getEmail())){

            form.reject("Email address is alrady taken");
            return badRequest(add.render(form));
        } else {
            createUser(form);

            flash("info", "You should have received an E-Mail confirmation, please click on the link in the E-Mail");
            return redirect(routes.UsersController.login());
        }
    }

    private boolean isEmailAddressTaken(String email) {
        return !userDao.isEmailAvailable(email);
    }

    private void createUser(Form<User> form) {
        User user = form.get();
        user.setId(UUID.randomUUID());
        user.setConfirmationToken(UUID.randomUUID().toString());
        userDao.persist(user);
    }
}
