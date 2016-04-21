package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;
import views.html.index;

public class Application extends Controller {

    public Result home() {
        return ok(home.render());
    }

    public Result index() {
        return ok(index.render());
    }

    public Result dashboard() {
        return ok(index.render());
    }
}
