package controllers;


import play.mvc.Controller;
import play.mvc.Result;
import views.html.edit;
import views.html.messageviewer;
import views.html.show;


public class Projects extends Controller {

    public Result index() {
        return ok(messageviewer.render());
    }

    public Result show(String id) {
        return ok(show.render());
    }

    public Result edit(String id) {
        //TODO load Project from database and return it
       return ok(edit.render());
    }

    public Result update(String id) {
        //TODO update project with that id
        return ok();
    }

    public Result createNew() {
        //TODO return empty Project
       return ok();
    }

    public Result create() {
        //TODO create Project
        return ok();
    }

    public Result delete(String id) {
        //TODO delete Project
        return ok();
    }
}
