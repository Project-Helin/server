package commons;

import com.google.inject.Inject;
import dao.DroneDao;
import dao.OrganisationsDao;
import models.Drone;
import models.Organisation;
import play.db.jpa.JPAApi;

import java.util.UUID;

public class TestHelper {

    @Inject
    OrganisationsDao organisationsDao;

    @Inject
    DroneDao droneDao;

    @Inject
    JPAApi jpaApi;

    public Organisation createNewOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
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

}
