package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.BodyParser;
import play.mvc.Controller;

public class DronesController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public play.mvc.Result create () {
        JsonNode json = request().body().asJson();
        String name = json.findPath("name").textValue();
        if(name == null) {
            return badRequest("Missing parameter [name]");
        } else {
            return ok();
        }
    }
}
