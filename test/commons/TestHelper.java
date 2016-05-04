package commons;

import ch.helin.messages.dto.state.DroneState;
import com.google.inject.Inject;
import dao.*;
import models.*;
import play.db.jpa.JPAApi;

import java.util.HashSet;
import java.util.UUID;

public class TestHelper {

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private DroneDao droneDao;

    @Inject
    private UserDao userDao;

    @Inject
    private JPAApi jpaApi;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private ProjectsDao projectsDao;

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

    public Drone createNewDrone(Organisation organisation) {
        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setName("Super HSR Drone" + System.currentTimeMillis());
        drone.setPayload(400);
        drone.setToken(UUID.randomUUID());

        drone.setOrganisation(organisation);

        jpaApi.withTransaction(() -> {
            droneDao.persist(drone);
        });

        return drone;
    }

    public User createUser(String plainTextPassword) {
        User user = new User();

        user.setId(UUID.randomUUID());
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

        user.setId(UUID.randomUUID());
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
        product.setId(UUID.randomUUID());
        product.setName("This is a product");
        product.setPrice(10d);
        product.setWeightGramm(100);

        product.setOrganisation(newOrganisation);

        jpaApi.withTransaction(() -> productsDao.persist(product));
        return product;
    }

    public Project createNewProject(Organisation organisation) {
        // force to use zone function wiht empty zones
        return createNewProject(organisation, new Zone[]{});
    }

    public Project createNewProject(Organisation organisation, Drone... drones) {

        Project project = new Project();
        project.setId(UUID.randomUUID());
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
        project.setId(UUID.randomUUID());
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
        project.setId(UUID.randomUUID());
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
        zone.setId(UUID.randomUUID());
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

}
