package controllers;

import play.libs.Json;
import play.mvc.Result;

import static play.mvc.Results.ok;

public class RegisterDroneController {

    public Result index() {
        //MySimpleMessage mySimpleMessage = new MySimpleMessage();
        //mySimpleMessage.setRaw("This is a test message!");
        return ok(Json.toJson(null));
    }

}
