package controllers;


import play.mvc.Controller;
import play.mvc.Result;
import views.html.messageviewer;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class MessageViewer extends Controller {

    public Result index() {
        return ok(messageviewer.render());
    }

}
