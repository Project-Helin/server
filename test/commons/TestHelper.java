package service;

import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.way.Position;
import com.google.inject.Inject;
import service.gis.GisHelper;
import dao.*;
import models.*;
import org.geolatte.geom.Polygon;
import play.db.jpa.JPAApi;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

/**
 * This class provides helper methods to create sample entities.
 */
public class TestHelper {

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private DroneDao droneDao;

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private OrderDao orderDao;

    @Inject
    private RouteDao routeDao;

    @Inject
    private OrderProductDao orderProductDao;

    @Inject
    private UserDao userDao;

    @Inject
    private JPAApi jpaApi;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private DroneInfoDao droneInfoDao;

    @Inject
    private CustomerDao customerDao;

    public Organisation createNewOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setToken(UUID.randomUUID().toString().substring(0, 5));
        organisation.setName("Super HSR " + System.currentTimeMillis());

        organisationsDao.persist(organisation);

        return organisation;
    }

    public Order createNewOrder(Project project, Customer customer) {
        Order order = new Order();
        order.setProject(project);
        order.setCustomer(customer);
        order.setState(OrderState.NEW);

        orderDao.persist(order);

        return order;
    }

    public Order createNewOrderWithThreeMissions(Project project, Customer customer) {
        Order order = new Order();
        order.setProject(project);
        order.setCustomer(customer);
        Position customerPos = new Position(47, 8);
        order.setCustomerPosition(GisHelper.createPoint(customerPos.getLon(), customerPos.getLat()));
        order.setState(OrderState.NEW);

        orderDao.persist(order);

        createNewMission(order);
        createNewMission(order);
        createNewMission(order);

        orderDao.persist(order);

        return order;
    }

    public Mission createNewMission(Order order) {
        Mission mission = new Mission();
        mission.setOrderProduct(createNewOrderProduct());
        mission.setOrder(order);

        Route route = new Route();

        routeDao.persist(route);
        missionsDao.persist(mission);

        route.setMission(mission);
        routeDao.persist(route);

        return mission;
    }

    public Drone createNewDrone(Organisation organisation) {
        Drone drone = new Drone();
        drone.setName("Super HSR Drone" + System.currentTimeMillis());
        drone.setPayload(400);
        drone.setToken(UUID.randomUUID());

        drone.setOrganisation(organisation);

        droneDao.persist(drone);

        return drone;
    }

    public Drone createNewDroneForProject(Project project, boolean isActive) {
        Drone drone = new Drone();
        drone.setName("Super HSR Drone" + System.currentTimeMillis());
        drone.setPayload(400);
        drone.setToken(UUID.randomUUID());
        drone.setIsActive(isActive);

        drone.setOrganisation(project.getOrganisation());
        drone.setProject(project);

        droneDao.persist(drone);
        return drone;
    }

    public User createUser(String plainTextPassword) {
        User user = new User();
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setName("Burce Wayne");
        user.setEmail("batman@wayneenterprise");
        user.setPassword(plainTextPassword);

        userDao.persist(user);

        return user;
    }

    public User createUserWithOrganisation(String plainTextPassword) {
        return createUserWithOrganisation(plainTextPassword, createNewOrganisation());
    }

    public User createUserWithOrganisation(String plainTextPassword, Organisation organisation) {
        User user = new User();
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setName("Bruce Wayne");
        user.setEmail("batman@wayneenterprise");
        user.setPassword(plainTextPassword);

        userDao.persist(user);
        organisation.getAdministrators().add(user);
        jpaApi.em().merge(organisation);
        jpaApi.em().flush();
        jpaApi.em().refresh(user);
        user.getOrganisations().size();

        return user;
    }


    public Product createProduct() {
        return createProduct(createNewOrganisation());
    }

    public Product createProduct(Organisation newOrganisation) {
        return createProduct(newOrganisation, 1);
    }

    public Product createProduct(Organisation newOrganisation, int maxItemPerDrone) {
        Product product = new Product();
        product.setName("This is a product");
        product.setPrice(10d);
        product.setWeightGramm(100);
        product.setMaxItemPerDrone(maxItemPerDrone);

        product.setOrganisation(newOrganisation);

        productsDao.persist(product);
        return product;
    }

    public Project createNewProject(Organisation organisation) {
        // force to use zone function with empty zones
        return createNewProject(organisation, new Zone[]{});
    }

    public Project createNewProject(Organisation organisation, Drone... drones) {

        Project project = new Project();
        project.setName("First Demo");
        project.setDrones(new HashSet<>());

        for (Drone each : drones) {
            each.setProject(project);
            project.getDrones().add(each);
        }

        project.setOrganisation(organisation);
        projectsDao.persist(project);


        return project;
    }

    public Project createNewProjectWithTwoZones(Organisation organisation) {
        return createNewProject(
            organisation,
            createUnsavedZone(
                "Loading Zone",
                ZoneType.LoadingZone,
                createSamplePolygon()
            ),
            createUnsavedZone(
                "Delivery Zone",
                ZoneType.DeliveryZone,
                createSamplePolygon()
            )
        );
    }

    public Project createNewProject(Organisation organisation, Zone... zones) {

        Project project = new Project();
        project.setName("First Demo");
        /**
         * we need to set the Project to the zone
         */
        for (Zone each : zones) {
            each.setProject(project);
            project.getZones().add(each);
        }

        project.setOrganisation(organisation);
        projectsDao.persist(project);

        return project;
    }

    public Project createNewProject(Organisation organisation, Product... products) {
        Project project = new Project();
        project.setName("First Demo");
        project.setProducts(new HashSet<>());

        for (Product each : products) {
            project.getProducts().add(each);
        }

        project.setOrganisation(organisation);
        projectsDao.persist(project);

        return project;
    }

    public Zone createUnsavedZone(String name, ZoneType type) {
        return createUnsavedZone(name, type, null);
    }

    public Zone createUnsavedZone(String name, ZoneType type, Polygon polygon) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setType(type);
        zone.setHeight(100);
        zone.setPolygon(polygon);

        return zone;
    }

    public DroneState getDroneState() {
        DroneState droneState = new DroneState();
        droneState.setAltitude(12);
        droneState.setIsConnected(true);
        droneState.setGroundSpeed(35);
        droneState.setFirmeware("Ardupilot 3.3");
        droneState.setVerticalSpeed(3);
        return droneState;
    }

    public OrderProduct createNewOrderProduct() {
        Product product = createProduct();

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setAmount(1);
        orderProduct.setTotalPrice(50.0);
        orderProduct.setProduct(product);
        return orderProduct;

    }

    public DroneInfo createDroneInfo(Drone drone) {
        DroneInfo droneInfo = new DroneInfo();

        droneInfo.setAltitude(10);
        droneInfo.setRemainingBatteryPercent(10);
        droneInfo.setClientTime(new Date());
        droneInfo.setBatteryVoltage(11.5);
        droneInfo.setDronePosition(GisHelper.createPoint(12.1, 45.2));
        droneInfo.setPhonePosition(GisHelper.createPoint(12.2, 45.1));

        droneInfo.setDrone(drone);

        droneInfoDao.persist(droneInfo);

        return droneInfo;
    }

    public Customer createCustomer() {
        return createCustomer("Bruce", "Wayne");
    }

    public Customer createCustomer(String givenName, String familyName) {
        Customer customer = new Customer();
        customer.setGivenName(givenName);
        customer.setFamilyName(familyName);
        customer.setEmail("testcustomer@helin.ch");
        customerDao.persist(customer);

        return customer;
    }

    /**
     * This polygon looks like this: https://upload.wikimedia.org/wikipedia/commons/3/3f/SFA_Polygon.svg
     */
    public Polygon createSamplePolygon() {
        return GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))");
    }

    public Drone createDroneWithAssignedMission(){
        Customer customer = this.createCustomer();
        Project project = this.createNewProject(this.createNewOrganisation());
        Order order = this.createNewOrderWithThreeMissions(project, customer);
        Drone newDrone = this.createNewDroneForProject(project, true);
        Mission newMission = this.createNewMission(order);

        newDrone.setCurrentMission(newMission);
        newMission.setDrone(newDrone);

        droneDao.persist(newDrone);
        missionsDao.persist(newMission);

        return newDrone;
    }
}
