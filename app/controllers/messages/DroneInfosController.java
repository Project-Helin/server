package controllers.messages;

import ch.helin.messages.dto.message.*;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import commons.order.MissionDispatchingService;
import commons.websocket.MissionWebSocketManager;
import dao.DroneDao;
import dao.DroneInfoDao;
import mappers.DroneInfoMapper;
import mappers.DroneMapper;
import models.Drone;
import models.DroneInfo;
import models.Mission;
import play.db.jpa.JPAApi;

import java.util.UUID;


public class DroneInfosController {
    @Inject
    private DroneDao droneDao;

    @Inject
    private DroneInfoDao droneInfoDao;

    @Inject
    private DroneInfoMapper droneInfoMapper;

    @Inject
    private DroneMapper droneMapper;

    @Inject
    private JPAApi jpaApi;

    @Inject
    MissionWebSocketManager webSocketManager;

    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    public void onDroneInfoReceived(UUID droneId, DroneInfoMessage droneInfoMessage) {
        jpaApi.withTransaction(()-> {
            DroneInfo droneInfo = droneInfoMapper.convertToDroneInfo(droneInfoMessage);

            Drone drone = droneDao.findById(droneId);
            droneInfo.setDrone(drone);

            Mission currentMission = drone.getCurrentMission();

            if(currentMission != null) {
                droneInfo.setMission(currentMission);
                webSocketManager.sendDroneInfoToConnectedClients(currentMission.getId(), droneInfoMessage);
            }

            droneInfoDao.persist(droneInfo);
        });
    }

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
}
