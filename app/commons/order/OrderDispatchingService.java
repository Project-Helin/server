package commons.order;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.way.RouteDto;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import dao.DroneDao;
import dao.MissionsDao;
import mappers.RouteMapper;
import models.Drone;
import models.Mission;
import models.MissionState;
import models.Order;

import java.util.UUID;

public class OrderDispatchingService {
    @Inject
    DroneCommunicationManager droneCommunicationManager;

    @Inject
    DroneDao droneDao;

    @Inject
    RouteMapper routeMapper;

    @Inject
    MissionsDao missionsDao;

    public int tryToDispatchWaitingOrders(UUID projectId, UUID orderId) {

        /*
        TODO Load all orders with state "WAITING_FOR_FREE_DRONE" from project and
        execute tryAssignDroneForThisOrder for Each Drone in Order of updated_at attribute
        on order.
         */

        //TODO return number of waiting orders before mine
        return 0;

    }


    public boolean tryAssignDroneForThisOrder(Order order) {

        //get this information from order
        int minPayload = 500;
        UUID projectId = UUID.randomUUID();

        // find the best drone for the mission

        Drone matchingDrone = droneDao.findMatchingPayloadAndHighestBatteryRemain(projectId, minPayload);

        if(matchingDrone != null) {
            Mission mission = new Mission();
            mission.setState(MissionState.LOADING);
            missionsDao.persist(mission);

            matchingDrone.setCurrentMission(mission);
            droneDao.persist(matchingDrone);

            AssignMissionMessage assignMissionMessage = new AssignMissionMessage();
            RouteDto routeDto = routeMapper.convertToRouteDto(mission.getRoute());
            assignMissionMessage.setRouteDto(routeDto);
            droneCommunicationManager.sendMessageToDrone(matchingDrone.getId(), assignMissionMessage);

            return true;
        } else {
            return false;
        }

    }


}
