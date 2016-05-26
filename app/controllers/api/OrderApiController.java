package controllers.api;

import ch.helin.commons.AssertUtils;
import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.OrderDto;
import ch.helin.messages.dto.way.Position;
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
import org.slf4j.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Transactional
public class OrderApiController extends Controller {

    private static final Logger logger = getLogger(OrderApiController.class);

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
    private SessionHelper sessionHelper;

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Inject
    private RouteCalculationService routeCalculationService;

    @Inject
    private OrderMapper orderMapper;

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
        logger.info("=> create");
        String jsonNode = request().body().asJson().toString();
        OrderApiDto orderApiDto = parseJsonOrNull(jsonNode);

        if (orderApiDto == null) {
            logger.debug("Send wrong request back, because invalid json: {}", jsonNode);
            return forbidden("Wrong request");
        }

        try {
            OrderDto orderDto = createOrder(orderApiDto);
            return ok(Json.toJson(orderDto));
        } catch (RuntimeException e) {
            logger.warn("Failed to process input: {}", jsonNode);
            throw e;
        }
    }

    private OrderDto createOrder(OrderApiDto orderApiDto) {

        Order order = saveOrder(orderApiDto);

        List<Position> positionList = routeCalculationService.calculateRoute(orderApiDto.getCustomerPosition(), order.getProject());

        Route route = RouteHelper.positionListToRoute(positionList);

        addMissionsToOrder(order, route);
        orderDao.persist(order);

        return orderMapper.convertToOrderDto(order);
    }

    /*
    * An existing Order is set as confirmed
    * and the mission is sent to drone
    */
    public Result confirm(UUID orderID, UUID customerId) {
        logger.info("=> confirm order");
        Order order = orderDao.findById(orderID);
        if (order == null) {
            return forbidden("Order not found");
        }

        Customer foundCustomer = customerDao.findById(customerId);
        if (foundCustomer == null) {
            return forbidden("Customer not found");
        }


        order.setState(OrderState.IN_PROGRESS);
        order.setCustomer(foundCustomer);
        order.getMissions().stream().forEach(mission -> {
            mission.setState(MissionState.WAITING_FOR_FREE_DRONE);
            missionsDao.persist(mission);
        });
        orderDao.persist(order);

        missionDispatchingService.tryToDispatchWaitingMissions(order.getProject().getId());

        return ok();
    }

    public Result delete(UUID orderID) {
        Order order = orderDao.findById(orderID);
        if (order == null) {
            return forbidden("Order not found");
        }
        logger.info("Delete order with id {}", orderID);
        orderDao.delete(order);
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


    private Order saveOrder(OrderApiDto orderApiDto) {
        Project project =
            projectsDao.findById(UUID.fromString(orderApiDto.getProjectId()));
        AssertUtils.throwExceptionIfNull(project, "Project not found");

        Order order = new Order();
        Position customerPosition = orderApiDto.getCustomerPosition();
        order.setCustomerPosition(GisHelper.createPoint(customerPosition.getLat(), customerPosition.getLon()));
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
