package commons;

import ch.helin.messages.dto.state.DroneState;
import com.google.inject.Inject;
import commons.gis.GisHelper;
import dao.*;
import models.*;
import org.apache.commons.lang3.RandomStringUtils;
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

        jpaApi.withTransaction(() -> {
            organisationsDao.persist(organisation);
        });

        return organisation;
    }

    public Order createNewOrder(Project project, Customer customer) {
        Order order = new Order();
        order.setProject(project);
        order.setCustomer(customer);

        jpaApi.withTransaction(() -> {
            orderDao.persist(order);
        });

        return order;
    }

    public Order createNewOrderWithThreeMissions(Project project, Customer customer) {
        Order order = new Order();
        order.setProject(project);
        order.setCustomer(customer);

        jpaApi.withTransaction(() -> {
            orderDao.persist(order);
        });

        createNewMission(order);
        createNewMission(order);
        createNewMission(order);

        return order;
    }

    public Mission createNewMission(Order order) {
        Mission mission = new Mission();
        mission.setOrderProduct(createNewOrderProduct());
        mission.setOrder(order);

        Route route = new Route();

        jpaApi.withTransaction(() -> {
            routeDao.persist(route);
            missionsDao.persist(mission);

            route.setMission(mission);
            routeDao.persist(route);
        });

        return mission;
    }

    public Drone createNewDrone(Organisation organisation) {
        Drone drone = new Drone();
        drone.setName("Super HSR Drone" + System.currentTimeMillis());
        drone.setPayload(400);
        drone.setToken(UUID.randomUUID());

        drone.setOrganisation(organisation);

        jpaApi.withTransaction(() -> {
            droneDao.persist(drone);
        });

        return drone;
    }

    public Drone createNewDroneForProject(Project project) {
        Drone drone = new Drone();
        drone.setName("Super HSR Drone" + System.currentTimeMillis());
        drone.setPayload(400);
        drone.setToken(UUID.randomUUID());

        drone.setOrganisation(project.getOrganisation());
        drone.setProject(project);

        jpaApi.withTransaction(() -> {
            droneDao.persist(drone);
        });

        return drone;
    }

    public User createUser(String plainTextPassword) {
        User user = new User();
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setName("Anna Bolika");
        user.setEmail("anna.bolika@example.com");
        user.setPassword(plainTextPassword);

        jpaApi.withTransaction(() -> {
            userDao.persist(user);
        });

        return user;
    }

    public User createUserWithOrganisation(String plainTextPassword) {
        return createUserWithOrganisation(plainTextPassword, createNewOrganisation());
    }

    public User createUserWithOrganisation(String plainTextPassword, Organisation organisation) {
        User user = new User();
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setName("Anna Bolika");
        user.setEmail("anna.bolika@example.com");
        user.setPassword(plainTextPassword);

        jpaApi.withTransaction(() -> {
            userDao.persist(user);
            organisation.getAdministrators().add(user);
            jpaApi.em().merge(organisation);
            jpaApi.em().flush();
            jpaApi.em().refresh(user);
            user.getOrganisations().size();
        });

        return user;
    }


    public Product createProduct() {
        return createProduct(createNewOrganisation());
    }

    public Product createProduct(Organisation newOrganisation) {
        Product product = new Product();
        product.setName("This is a product");
        product.setPrice(10d);
        product.setWeightGramm(100);

        product.setOrganisation(newOrganisation);

        jpaApi.withTransaction(() -> productsDao.persist(product));
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

        jpaApi.withTransaction(() -> {
            project.setOrganisation(organisation);
            projectsDao.persist(project);
        });

        return project;
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

        jpaApi.withTransaction(() -> {
            project.setOrganisation(organisation);
            projectsDao.persist(project);
        });

        return project;
    }

    public Project createNewProject(Organisation organisation, Product... products) {
        Project project = new Project();
        project.setName("First Demo");
        project.setProducts(new HashSet<>());

        for (Product each : products) {
            project.getProducts().add(each);
        }

        jpaApi.withTransaction(() -> {
            project.setOrganisation(organisation);
            projectsDao.persist(project);
        });

        return project;
    }

    public Zone createUnsavedZone(String name, ZoneType type) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setType(type);
        zone.setHeight(100);

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
        Customer customer = new Customer();
        customer.setDisplayName("Testcustomer");
        customer.setEmail("testcustomer@helin.ch");
        customer.setToken(RandomStringUtils.randomAlphanumeric(10));

        jpaApi.withTransaction(() -> {
            customerDao.persist(customer);
        });

        return customer;
    }
}
