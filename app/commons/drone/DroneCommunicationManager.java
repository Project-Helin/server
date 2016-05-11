package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.message.Message;
import dao.DroneDao;
import models.Drone;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class DroneCommunicationManager {

    private final JPAApi jpaApi;
    private DroneMessageDispatcher droneMessageDispatcher;
    private final JsonBasedMessageConverter messageConverter;
    private DroneDao droneDao;
    private HashMap<UUID, DroneConnection> droneConnections = new HashMap<>();

    @Inject
    public DroneCommunicationManager(DroneDao droneDao, JPAApi jpaApi, DroneMessageDispatcher droneMessageDispatcher, JsonBasedMessageConverter messageConverter) {
        this.droneDao = droneDao;
        this.jpaApi = jpaApi;
        this.droneMessageDispatcher = droneMessageDispatcher;
        this.messageConverter = messageConverter;
        jpaApi.withTransaction(() -> {
            droneDao.findAll().stream().forEach(this::addDrone);
        });
    }

    public void addDrone(Drone drone) {
        droneConnections.put(drone.getId(), new DroneConnection(drone, droneMessageDispatcher));
    }

    public void sendMessageToDrone(UUID droneId, Message message) {
        DroneConnection droneConnection = droneConnections.get(droneId);
        String messageAsString = messageConverter.parseMessageToString(message);
        droneConnection.sendMessage(messageAsString);
    }


}
