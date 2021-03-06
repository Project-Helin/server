package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.routeDebugger;

public class Application extends Controller {

    public Result index() {
        return ok(index.render());
    }

    public Result routeDebugger(String projectId) {

        return ok(routeDebugger.render(projectId));
    }
}
