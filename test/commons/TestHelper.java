package commons;

import com.google.inject.Inject;
import dao.*;
import models.*;
import play.db.jpa.JPAApi;

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
        organisation.setToken(UUID.randomUUID().toString().substring(0,5));
        organisation.setName("Super HSR " + System.currentTimeMillis());

        jpaApi.withTransaction(() -> {
            organisationsDao.persist(organisation);
        });

        return organisation;
    }

    public Drone createNewDrone() {
        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setName("Super HSR Drone" + System.currentTimeMillis());
        drone.setPayload(400);
        drone.setToken(UUID.randomUUID());

        Organisation organisation = this.createNewOrganisation();

        drone.setOrganisation(organisation);

        jpaApi.withTransaction(() -> {
            droneDao.persist(drone);
        });

        return drone;
    }

    public User createUserWithOrganisation(String plainTextPassword) {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setName("Anna Bolika");
        user.setEmail("anna.bolika@example.com");
        user.setPassword(plainTextPassword);

        jpaApi.withTransaction(() -> {
            Organisation organisation = createNewOrganisation();
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
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("This is a product");
        product.setPrice(10d);
        product.setWightGramm(100);

        product.setOrganisation(
            createNewOrganisation()
        );

        jpaApi.withTransaction(() -> productsDao.persist(product));
        return product;
    }

    public Project createNewProject(User user) {

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("First Demo");

        jpaApi.withTransaction(() -> {
            Organisation organisation = user.getOrganisations().stream().findFirst().get();
            project.setOrganisation(organisation);
            projectsDao.persist(project);
        });

        return project;
    }
}
