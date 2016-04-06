package controllers;

import play.libs.Json;
import play.mvc.Result;
import views.html.messageviewer;

import static play.mvc.Results.ok;

/**
 * Created by Martin Stypinski on 02.04.16.
 */
public class RegisterController {

    public Result index() {
        //MySimpleMessage mySimpleMessage = new MySimpleMessage();
        //mySimpleMessage.setRaw("This is a test message!");
        return ok(Json.toJson(null));
    }

}
