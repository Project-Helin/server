package commons.drone;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DroneDao;
import models.Drone;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class DroneCommunicationManager {

    private DroneDao droneDao;

    HashMap<UUID, DroneConnection> droneConnections = new HashMap<>();

    @Inject
    public DroneCommunicationManager(DroneDao droneDao) {
        this.droneDao = droneDao;

        droneDao.findAll().stream().forEach(this::addDrone);

        //Loop over all active drones and create a droneconnection for each. Then add them to droneConnections
    }

    public void addDrone(Drone drone) {
        droneConnections.put(drone.getId(), new DroneConnection(drone.getToken()));
    }


}
