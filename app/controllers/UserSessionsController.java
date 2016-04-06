package controllers;

import com.google.inject.Inject;
import models.authentication.Login;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.userSessions.login;

public class UserSessionsController extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result login() {
        Form<Login> form = formFactory
                .form(Login.class);

        return ok(login.render(form));
    }

    public Result create() {
        return ok();
    }



}
