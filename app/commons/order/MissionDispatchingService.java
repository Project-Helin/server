package commons.order;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
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
    private DroneCommunicationManager droneCommunicationManager;

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


    public boolean tryAssignDroneForThisMission(Mission mission) {

        int minPayload = mission.getOrderProduct().getAmount() * mission.getOrderProduct().getProduct().getWeightGramm();
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

            droneCommunicationManager.sendMessageToDrone(matchingDrone.getId(), assignMissionMessage);

            return true;
        } else {
            return false;
        }

    }


}