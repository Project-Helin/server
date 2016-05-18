package controllers;

import ch.helin.messages.dto.message.missionMessage.*;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import commons.order.MissionDispatchingService;
import dao.DroneDao;
import dao.MissionsDao;
import mappers.MissionMapper;
import models.Drone;
import models.Mission;
import models.MissionState;
import play.db.jpa.JPAApi;

import java.util.UUID;


public class MissionController{
    @Inject
    private DroneDao droneDao;

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private JPAApi jpaApi;

    @Inject
    private MissionMapper missionMapper;

    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    @Inject
    private MissionDispatchingService missionDispatchingService;

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

    public void onFinishedMissionMessageReceived(UUID droneId, FinishedMissionMessage message) {
        jpaApi.withTransaction(() -> {
            boolean successful = message.getFinishedType() == MissionFinishedType.SUCCESSFUL;

            Drone drone = droneDao.findById(droneId);
            Mission mission = drone.getCurrentMission();

            if (successful) {
                mission.setState(MissionState.DELIVERED);
                drone.setCurrentMission(null);
            } else {
                mission.setState(MissionState.WAITING_FOR_FREE_DRONE);
                drone.setCurrentMission(null);
            }

            missionsDao.persist(mission);
            droneDao.persist(drone);
        });
    }

}
