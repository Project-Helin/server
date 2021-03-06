package service.order;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import com.google.inject.Inject;
import com.google.inject.Provider;
import service.drone.DroneCommunicationManager;
import dao.DroneDao;
import dao.MissionsDao;
import mappers.MissionMapper;
import models.Drone;
import models.Mission;
import models.MissionState;

import java.util.List;
import java.util.UUID;

public class MissionDispatchingService {

    @Inject
    private Provider<DroneCommunicationManager> droneCommunicationManagerProvider;

    @Inject
    private DroneDao droneDao;

    @Inject
    private MissionMapper missionMapper;

    @Inject
    private MissionsDao missionsDao;

    public void tryToDispatchWaitingMissions(UUID projectId) {
        List<Mission> waitingMissions = missionsDao.findWaitingMissions(projectId);
        waitingMissions.stream().forEach(this::tryAssignDroneForThisMission);
    }

    public void tryAssignDroneForThisMission(Mission mission) {

        Integer amount = mission.getOrderProduct().getAmount();
        Integer weightGramm = mission.getOrderProduct().getProduct().getWeightGramm();

        int minPayload = amount * weightGramm;
        UUID projectId = mission.getOrder().getProject().getId();

        // find the best drone for the mission
        Drone matchingDrone = droneDao.findMatchingPayloadAndHighestBatteryRemain(projectId, minPayload);

        if(matchingDrone != null) {
            mission.setState(MissionState.WAITING_FOR_DRONE_CONFIRMATION);
            mission.setDrone(matchingDrone);
            missionsDao.persist(mission);

            matchingDrone.setCurrentMission(mission);
            droneDao.persist(matchingDrone);

            AssignMissionMessage assignMissionMessage = new AssignMissionMessage();
            assignMissionMessage.setMission(missionMapper.convertToMissionDto(mission));

            droneCommunicationManagerProvider.get().sendMessageToDrone(matchingDrone.getId(), assignMissionMessage);
        }
    }

    public void withdrawDroneFromMission(Drone drone){
        if(drone.getCurrentMission() != null){
            UUID missionId = drone.getCurrentMission().getId();
            Mission currentMissionAssignedToDrone = missionsDao.findById(missionId);
            currentMissionAssignedToDrone.setDrone(null);
            currentMissionAssignedToDrone.setState(MissionState.WAITING_FOR_FREE_DRONE);
            drone.setCurrentMission(null);

            droneDao.persist(drone);
            missionsDao.persist(currentMissionAssignedToDrone);
        }

    }

}
