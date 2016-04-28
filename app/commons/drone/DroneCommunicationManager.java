package commons.drone;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DroneDao;
import models.Drone;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class DroneCommunicationManager {

    private DroneMessageDispatcher droneMessageDispatcher;
    private DroneDao droneDao;
    private HashMap<UUID, DroneConnection> droneConnections = new HashMap<>();

    @Inject
    public DroneCommunicationManager(DroneDao droneDao, DroneMessageDispatcher droneMessageDispatcher) {
        this.droneDao = droneDao;
        this.droneMessageDispatcher = droneMessageDispatcher;

        droneDao.findAll().stream().forEach(this::addDrone);
    }

    public void addDrone(Drone drone) {
        droneConnections.put(drone.getId(), new DroneConnection(drone, droneMessageDispatcher));
    }


}
