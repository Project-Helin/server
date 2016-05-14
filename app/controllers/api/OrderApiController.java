package controllers.api;

import ch.helin.messages.dto.way.RouteDto;
import com.google.gson.Gson;
import com.google.inject.Inject;
import commons.order.MissionDispatchingService;
import commons.routeCalculationService.RouteCalculationService;
import dao.*;
import dto.api.OrderApiDto;
import mappers.RouteMapper;
import models.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Inject
    private RouteDao routeDao;

    @Inject
    private RouteMapper routeMapper;

    @Inject
    private RouteCalculationService routeCalculationService;


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
        OrderApiDto orderApiDto = new Gson().fromJson(jsonNode, OrderApiDto.class);

        if (orderApiDto == null) {
            logger.debug("Send wrong request back, because invalid json: {}", jsonNode);
            return forbidden("Wrong request");
        }

        Customer customer = createCustomer(orderApiDto);
        customerDao.persist(customer);

        Order order = createOrder(orderApiDto, customer);
        orderDao.persist(order);

        Route proposedRoute = calculateRoute();

        Set<OrderProduct> orderProducts = order.getOrderProducts();
        Mission mission = new Mission();
        mission.setOrder(order);
        mission.setState(MissionState.NEW);
        mission.setOrderProduct(orderProducts.iterator().next()); // TODO fix
        mission.setRoute(proposedRoute);
        HashSet<Mission> missions1 = new HashSet<>();
        missions1.add(mission);
        order.setMissions(missions1);

        // mision to OrderProduct assignment
        // one orderproduct per mission
        // split order-products -> add more orders (

        //Set State to ROUTE_SUGGESTED
        //Split order in Missions based on maxamount on product and on highest payload of a drone in project.

        //Calculate Route

        //Send route to Customer
//        Route route =
//            routeCalculationService.calculateRoute(orderApiDto.getCustomerPosition(), order.getProject());
//
//        Set<Mission> missions = order.getMissions();
//
//        for (Mission each : missions) {
//            Route route = new Route();
//            route.setMission(each);
//            route.setWayPoints(route.getWayPoints());;
//            each.setRoute(route);
//            routeDao.persist(route);
//            missionsDao.persist(each);
//        }
//
//        return ok(Json.toJson(routeDto));
        return null;
    }

    private Route calculateRoute() {
        return new Route();
    }

    private Order createOrder(OrderApiDto orderApiDto, Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);

        // TODO fix this: does customer provide project-id?
        Project first = projectsDao.findAll().iterator().next();
        order.setProject(first);
        order.setState(OrderState.ROUTE_SUGGESTED);
        ;
        order.setOrderProducts(getOrderProducts(orderApiDto, order));
        return order;
    }

    private Customer createCustomer(OrderApiDto orderApiDto) {
        Customer customer = new Customer();
        customer.setDisplayName(orderApiDto.getDisplayName());
        customer.setEmail(orderApiDto.getEmail());
        customer.setToken(RandomStringUtils.randomAlphanumeric(10));// TODO Fix this
        return customer;
    }

    private Set<OrderProduct> getOrderProducts(OrderApiDto orderApiDto, Order newOrder) {
        return orderApiDto.getOrderProducts().stream().map((each) -> {
            Product product = productsDao.findById(UUID.fromString(each.getProductId()));

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setAmount(each.getAmount());
            orderProduct.setOrder(newOrder);
            orderProduct.setProduct(product);
            orderProduct.setTotalPrice(product.getPrice() * each.getAmount());

            return orderProduct;
        }).collect(Collectors.toSet());
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
