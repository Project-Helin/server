package controllers.api;

import ch.helin.messages.commons.AssertUtils;
import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.OrderDto;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Inject;
import commons.SessionHelper;
import commons.gis.GisHelper;
import commons.order.MissionDispatchingService;
import commons.routeCalculationService.RouteCalculationService;
import dao.*;
import dto.api.OrderApiDto;
import dto.api.OrderProductApiDto;
import mappers.OrderMapper;
import mappers.RouteMapper;
import models.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Transactional
public class OrderApiController extends Controller {

    private static final Logger logger = getLogger(OrderApiController.class);

    @Inject
    private OrderDao orderDao;

    @Inject
    private RouteMapper routeMapper;

    @Inject
    private OrderMapper orderMapper;

    @Inject
    private CustomerDao customerDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Inject
    private RouteCalculationService routeCalculationService;

    public Result show(UUID orderId) {

        Order order = orderDao.findById(orderId);

        if (order.getProject().getOrganisation() != sessionHelper.getOrganisation(session())) {
            return forbidden();
        } else {
            OrderDto orderDto = orderMapper.convertToOrderDto(order);
            return ok(Json.toJson(orderDto));
        }
    }


    /*
     * An Order with mission and route is created,
     * but it should not be sent to the drone.
     * The customer should receive an offer for
     * the deliveryLocation first
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result create() {
        String jsonNode = request().body().asJson().toString();
        OrderApiDto orderApiDto = parseJsonOrNull(jsonNode);

        if (orderApiDto == null) {
            logger.debug("Send wrong request back, because invalid json: {}", jsonNode);
            return forbidden("Wrong request");
        }

        try {
            OrderApiOutputDto routeDto = createOrder(orderApiDto);
            return ok(Json.toJson(routeDto));
        } catch (RuntimeException e) {
            logger.warn("Failed to process input: {}", jsonNode);
            throw e;
        }
    }

    private OrderApiOutputDto createOrder(OrderApiDto orderApiDto) {

        //TODO should not be here, the customer should only be loaded
        Customer customer = saveCustomer(orderApiDto);

        Order order = saveOrder(orderApiDto, customer);

        List<Position> positionList = routeCalculationService.calculateRoute(orderApiDto.getCustomerPosition(), order.getProject());

        Route route = RouteHelper.positionListToRoute(positionList);

        addMissionsToOrder(order, route);
        orderDao.persist(order);

        WayPoint last = route.getWayPoints().stream().reduce((a, b) -> b).orElse(null);

        RouteDto routeDto = routeMapper.convertToRouteDto(route);

        OrderApiOutputDto orderApiOutputDto = new OrderApiOutputDto();
        orderApiOutputDto.setRoute(routeDto);
        orderApiOutputDto.setDeliveryPosition(GisHelper.createPosition(last.getPosition()));
        orderApiOutputDto.setOrderId(order.getIdAsString());
        return orderApiOutputDto;
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

    private OrderApiDto parseJsonOrNull(String rawJson) {
        try {
            return new Gson().fromJson(rawJson, OrderApiDto.class);
        } catch (Exception e) {
            logger.warn("Failed to parse json {}", rawJson, e);
            return null;
        }
    }

    private Customer saveCustomer(OrderApiDto orderApiDto) {
        Customer customer = new Customer();
        customer.setDisplayName(orderApiDto.getDisplayName());
        customer.setEmail(orderApiDto.getEmail());

        // TODO fix this -> see HEL 54
        customer.setToken(RandomStringUtils.randomAlphanumeric(10));
        customerDao.persist(customer);

        return customer;
    }

    private Order saveOrder(OrderApiDto orderApiDto, Customer customer) {
        Project project =
                projectsDao.findById(UUID.fromString(orderApiDto.getProjectId()));
        AssertUtils.throwExceptionIfNull(project, "Project not found");

        Order order = new Order();
        order.setCustomer(customer);
        Position customerPosition = orderApiDto.getCustomerPosition();
        order.setDeliveryPosition(GisHelper.createPoint(customerPosition.getLat(), customerPosition.getLon()));
        order.setProject(project);
        order.setState(OrderState.ROUTE_SUGGESTED);
        Set<OrderProduct> splitOrderProducts = splitAndConvertToOrderProductsBasedOnMaxAmountPerDrone(order, orderApiDto.getOrderProducts());
        order.setOrderProducts(splitOrderProducts);

        orderDao.persist(order);
        return order;
    }

    private void addMissionsToOrder(Order order, Route route) {
        Set<Mission> createdMissions =
                order.getOrderProducts()
                        .stream()
                        .map((orderProduct) -> {
                            Mission mission = new Mission();
                            mission.setOrder(order);
                            mission.setState(MissionState.NEW);
                            mission.setOrderProduct(orderProduct);
                            mission.setRoute(route);

                            route.setMission(mission);
                            mission.setRoute(route);
                            return mission;
                        })
                        .collect(Collectors.toSet());

        order.setMissions(createdMissions);
    }

    private Set<OrderProduct> splitAndConvertToOrderProductsBasedOnMaxAmountPerDrone(Order newOrder, List<OrderProductApiDto> orderProductDtos) {
        HashSet<OrderProduct> orderProducts = new HashSet<>();

        for (OrderProductApiDto orderProduct : orderProductDtos) {

            Product product = productsDao.findById(UUID.fromString(orderProduct.getId()));
            AssertUtils.throwExceptionIfNull(product, "Product not found");

            Integer amount = orderProduct.getAmount();
            AssertUtils.throwExceptionIfNull(amount, "Order amount cannot be null");

            boolean orderedAmountFitsOnOneDrone = amount < product.getMaxItemPerDrone();
            if (orderedAmountFitsOnOneDrone) {

                orderProducts.add(new OrderProduct(newOrder, product, amount));
            } else {
                // we need to split the order
                int neededOrderProducts = amount / product.getMaxItemPerDrone();
                for (int i = 0; i < neededOrderProducts; i++) {

                    orderProducts.add(new OrderProduct(newOrder, product, product.getMaxItemPerDrone()));
                }

                // for the remaining items
                int remaining = amount % product.getMaxItemPerDrone();
                boolean thereAreRestItems = remaining != 0;
                if (thereAreRestItems) {

                    orderProducts.add(new OrderProduct(newOrder, product, remaining));
                }
            }
        }

        return orderProducts;
    }

}
