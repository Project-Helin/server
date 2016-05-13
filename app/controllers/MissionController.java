package controllers;

import ch.helin.messages.dto.message.missionMessage.ConfirmMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.MissionConfirmType;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import dao.DroneDao;
import mappers.MissionMapper;
import models.Drone;
import models.Mission;
import models.MissionState;
import play.db.jpa.JPAApi;

import java.util.UUID;


public class MissionController {

    @Inject
    DroneDao droneDao;

    @Inject
    JPAApi jpaApi;

    @Inject
    MissionMapper missionMapper;

    @Inject
    DroneCommunicationManager droneCommunicationManager;


    public void onConfirmMissionMessageReceived(UUID droneId, ConfirmMissionMessage missionMessage) {
        jpaApi.withTransaction(() -> {
            boolean missionConfirmed = missionMessage.getMissionConfirmType() == MissionConfirmType.ACCEPT;
            Drone drone = droneDao.findById(droneId);

            Mission mission = drone.getCurrentMission();
            if (missionConfirmed) {
                mission.setState(MissionState.LOADING);
                FinalAssignMissionMessage finalAssignMissionMessage = new FinalAssignMissionMessage();
                finalAssignMissionMessage.setMission(missionMapper.convertToMissionDto(mission));
                droneCommunicationManager.sendMessageToDrone(drone.getId(), finalAssignMissionMessage);

            } else {
                mission.setState(MissionState.WAITING_FOR_FREE_DRONE);
                drone.setCurrentMission(null);
            }

            droneDao.persist(drone);
        });
    }
}
