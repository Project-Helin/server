package controllers;

import com.google.inject.Inject;
import dao.UserDao;
import models.User;
import play.db.jpa.Transactional;
import views.formdata.Login;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.userSessions.login;

public class UserSessionsController extends Controller {

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
    public Result create() {
        Form<Login> form = formFactory
                .form(Login.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            return badRequest(login.render(form));
        } else {
            User user = userDao.authenticateAndGetUser(form.get().getEmail(), form.get().getPassword());
            if (user == null) {
                flash("error","Wrong user or password");
                return badRequest(login.render(form));
            } else if (!user.isValidated()) {
               flash("error", "Account not validated, please check your email");
                return badRequest(login.render(form));
            } else {
                session("email", form.get().getEmail());
                redirect(routes.Application.index());
            }
        }

        return ok();
    }
}
