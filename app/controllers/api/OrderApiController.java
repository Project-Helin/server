package controllers.api;

import com.google.gson.Gson;
import com.google.inject.Inject;
import commons.order.MissionDispatchingService;
import dao.*;
import models.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Transactional
public class OrderApiController extends Controller {

    private static final Logger logger = getLogger(OrderApiController.class);

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Inject
    private OrderDao orderDao;

    @Inject
    private CustomerDao customerDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private MissionsDao missionsDao;

    /*
     * An Order with mission and route is created,
     * but it should not be sent to the drone.
     * The customer should receive an offer for
     * the deliveryLocation first
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public Result create() {
        String jsonNode = request().body().asJson().toString();
        OrderCargoDto orderCargoDto = new Gson().fromJson(jsonNode, OrderCargoDto.class);

        if (orderCargoDto == null) {
            logger.info("Send wrong request back, because invalid json: {}", orderCargoDto);
            return forbidden("Wrong request");
        }

        //Create Order
        Customer customer = new Customer();
        customer.setDisplayName(orderCargoDto.getDisplayName());
        customer.setEmail(orderCargoDto.getEmail());
        customerDao.persist(customer);

        // TODO Fix this
        customer.setToken(RandomStringUtils.randomAlphanumeric(10));

        Order order = new Order();
        order.setCustomer(customer);

        // TODO fix this: does customer provide project-id?
        Project first = projectsDao.findAll().iterator().next();
        order.setProject(first);

        order.setState(OrderState.ROUTE_SUGGESTED);;
        order.setOrderProducts(getOrderProducts(orderCargoDto, order));
        orderDao.persist(order);

        //Set State to ROUTE_SUGGESTED
        //Split order in Missions based on maxamount on product and on highest payload of a drone in project.
        //Calculate Route
        //Send route to Customer

        return ok();
    }

    private Set<OrderProduct> getOrderProducts(OrderCargoDto orderCargoDto, Order order) {
        List<ApiOrderProductDto> orderProducts = orderCargoDto.getOrderProducts();
        HashSet<OrderProduct> products = new HashSet<>();
        for (ApiOrderProductDto orderProduct : orderProducts) {
            OrderProduct e = new OrderProduct();
            e.setAmount(orderProduct.getAmount());;
            e.setOrder(order);
            Product byId = productsDao.findById(UUID.fromString(orderProduct.getProductId()));
            e.setProduct(byId);
            e.setTotalPrice(byId.getPrice() * orderProduct.getAmount());
            products.add(e);
        }
        return products;
    }

    /*
     * An existing Order is set as confirmed
     * and the mission is sent to drone
     */
    public Result confirm(UUID orderID) {
        Order order = orderDao.findById(orderID);
        if (order == null) {
            return forbidden("Order not found");
        }

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
