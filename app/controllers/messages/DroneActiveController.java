package controllers.messages;

import ch.helin.messages.dto.message.DroneActiveState;
import ch.helin.messages.dto.message.DroneActiveStateMessage;
import ch.helin.messages.dto.message.DroneDtoMessage;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import dao.DroneDao;
import mappers.DroneMapper;
import models.Drone;
import play.db.jpa.JPAApi;

import java.util.UUID;

public class DroneActiveController {

    @Inject
    private DroneDao droneDao;

    @Inject
    private DroneMapper droneMapper;

    @Inject
    private JPAApi jpaApi;

    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    public void onDroneActiveStateReceived(UUID droneId, DroneActiveStateMessage droneActiveStateMessage) {
        DroneActiveState droneActiveState = droneActiveStateMessage.getDroneActiveState();

        jpaApi.withTransaction(() -> {
            Drone drone = droneDao.findById(droneId);

            drone.setIsActive(droneActiveState.getActive());

            DroneDtoMessage droneDtoMessage = new DroneDtoMessage();
            droneDtoMessage.setDroneDto(droneMapper.getDroneDto(drone));

            droneDao.persist(drone);
            droneCommunicationManager.sendMessageToDrone(drone.getId(), droneDtoMessage);

        });
    }

    public void removeMissionFromDrone(){

    }

}
