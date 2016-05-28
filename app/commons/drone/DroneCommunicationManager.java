package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.message.Message;
import dao.DroneDao;
import models.Drone;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DroneCommunicationManager {

    private DroneMessageDispatcher droneMessageDispatcher;

    @Inject
    private JsonBasedMessageConverter messageConverter;

    private Map<UUID, DroneConnection> droneIdToConnection = new ConcurrentHashMap<>();

    @Inject
    public DroneCommunicationManager(DroneDao droneDao,
                                     JPAApi jpaApi,
                                     DroneMessageDispatcher droneMessageDispatcher) {

        this.droneMessageDispatcher = droneMessageDispatcher;
        jpaApi.withTransaction(() -> {
            droneDao.findAll().stream().forEach(this::addDrone);
        });

    }

    public void addDrone(Drone drone) {
        droneIdToConnection.put(drone.getId(), new DroneConnection(drone, droneMessageDispatcher));
    }

    public void sendMessageToDrone(UUID droneId, Message message) {
        DroneConnection droneConnection = droneIdToConnection.get(droneId);
        String messageAsString = messageConverter.parseMessageToString(message);
        droneConnection.sendMessage(messageAsString);
    }
}
