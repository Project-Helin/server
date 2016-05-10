package controllers.api;

import com.google.inject.Inject;
import commons.order.OrderDispatchingService;
import dao.OrderDao;
import models.Order;
import models.OrderState;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

@Transactional
public class OrderApiController extends Controller {

    @Inject
    OrderDispatchingService orderDispatchingService;

    @Inject
    OrderDao orderDao;

    /*
    An Order with mission and route is created,
    but it should not be sent to the drone.
    The customer should receive an offer for
    the deliveryLocation first
     */
    public Result create () {
        //Create Order
        //Set State to ROUTE_SUGGESTED
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
        Order order = orderDao.findById(orderID);
        order.setState(OrderState.WAITING_FOR_FREE_DRONE);
        orderDao.persist(order);

        orderDispatchingService.tryToDispatchWaitingOrders(order.getProject().getId(), order.getId());
        int countOfOrdersBeforeMe = orderDispatchingService.getMyNumberInWaitingQueue(order);

        return ok(Json.toJson(countOfOrdersBeforeMe));
    }

}
