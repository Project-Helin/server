package commons.drone;

import dao.DroneDao;
import models.Drone;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class DroneCommunicationManager {

    private DroneMessageDispatcher droneMessageDispatcher;
    private DroneDao droneDao;
    private HashMap<UUID, DroneConnection> droneConnections = new HashMap<>();

    @Inject
    public DroneCommunicationManager(DroneDao droneDao, JPAApi jpaApi, DroneMessageDispatcher droneMessageDispatcher) {
        this.droneDao = droneDao;
        this.droneMessageDispatcher = droneMessageDispatcher;
        jpaApi.withTransaction( () -> {
            droneDao.findAll().stream().forEach(this::addDrone);
        });
    }

    public void addDrone(Drone drone) {
        droneConnections.put(drone.getId(), new DroneConnection(drone, droneMessageDispatcher));
    }


}
