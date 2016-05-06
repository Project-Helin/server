package controllers.api;

import com.google.inject.Inject;
import commons.order.OrderDispatchingService;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

public class OrderApiController extends Controller {

    @Inject
    OrderDispatchingService orderDispatchingService;

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
        //Load Order
        //set Order to confirmed

        UUID orderId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        orderDispatchingService.tryToDispatchWaitingOrders(projectId, orderId);

        //return estimated deliveryTime
        return ok();
    }

}
