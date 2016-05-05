package controllers;

import ch.helin.messages.dto.message.DroneInfoMessage;
import com.google.inject.Inject;
import dao.DroneDao;
import dao.DroneInfoDao;
import mappers.DroneInfoMapper;
import models.Drone;
import models.DroneInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DroneInfosController {
    private static final Logger logger = LoggerFactory.getLogger(DroneInfosController.class);

    @Inject
    DroneDao droneDao;

    @Inject
    DroneInfoDao droneInfoDao;

    @Inject
    DroneInfoMapper droneInfoMapper;

    public void onDroneInfoReceived(UUID droneId, DroneInfoMessage droneInfoMessage) {

        DroneInfo droneInfo = droneInfoMapper.convertToDroneInfo(droneInfoMessage);

        Drone drone = droneDao.findById(droneId);
        droneInfo.setDrone(drone);

        if(drone.getCurrentMission() != null) {
            droneInfo.setMission(drone.getCurrentMission());
        }

        droneInfoDao.persist(droneInfo);
    }
}
