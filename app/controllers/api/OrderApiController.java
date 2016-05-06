package controllers.api;

import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

public class OrderApiController extends Controller {

    /*
    An Order with mission and rout is created,
    but it should not be sent to the drone.
    The customer should receive an offer for
    the deliveryLocation first
     */
    public Result create () {
        //Create Order
        //Create Mission
        //Calculate Route
        //Send route to Customer

        return ok();


    }

    /*
    An existing Order is set as confirmed
    and the mission is sent to drone
     */
    public Result confirm(UUID orderID) {
        //set Order to confirmed
        //send route to drone
        return ok();
    }

}
