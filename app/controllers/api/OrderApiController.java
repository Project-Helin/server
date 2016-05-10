package controllers.api;

import com.google.inject.Inject;
import commons.order.MissionDispatchingService;
import dao.MissionsDao;
import dao.OrderDao;
import models.MissionState;
import models.Order;
import models.OrderState;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

@Transactional
public class OrderApiController extends Controller {

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Inject
    private OrderDao orderDao;

    @Inject
    private MissionsDao missionsDao;

    /*
    An Order with mission and route is created,
    but it should not be sent to the drone.
    The customer should receive an offer for
    the deliveryLocation first
     */
    public Result create() {
        //Create Order
        //Set State to ROUTE_SUGGESTED
        //Split order in Missions based on maxamount on product and on highest payload of a drone in project.
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
        order.setState(OrderState.IN_PROGRESS);
        order.getMissions().stream().forEach(mission -> {
            mission.setState(MissionState.WAITING_FOR_FREE_DRONE);
            missionsDao.persist(mission);
        });

        orderDao.persist(order);

        missionDispatchingService.tryToDispatchWaitingMissions(order.getProject().getId());

        return ok();
    }

}
