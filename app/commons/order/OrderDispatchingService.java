package commons.order;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import dao.DroneDao;
import dao.MissionsDao;
import dao.OrderDao;
import mappers.MissionMapper;
import models.Drone;
import models.Mission;
import models.MissionState;
import models.Order;

import java.util.List;
import java.util.UUID;

public class OrderDispatchingService {
    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    @Inject
    private DroneDao droneDao;

    @Inject
    private OrderDao orderDao;

    @Inject
    private MissionMapper missionMapper;

    @Inject
    private MissionsDao missionsDao;

    public void tryToDispatchWaitingOrders(UUID projectId, UUID orderId) {

        List<Order> waitingOrders = orderDao.findWaitingOrders(projectId);
        waitingOrders.stream().forEach(this::tryAssignDroneForThisOrder);
    }

    public int getMyNumberInWaitingQueue (Order order) {
        List<Order> waitingOrders = orderDao.findWaitingOrders(order.getProject().getId());

        return waitingOrders.indexOf(orderDao.findById(order.getId()));
    }


    public boolean tryAssignDroneForThisOrder(Mission mission) {

        int minPayload = order.getOrderProducts().stream()
                .mapToInt((o) -> o.getAmount() * o.getProduct().getWeightGramm())
                .sum();

        UUID projectId = order.getProject().getId();

        // find the best drone for the mission

        Drone matchingDrone = droneDao.findMatchingPayloadAndHighestBatteryRemain(projectId, minPayload);

        if(matchingDrone != null) {
            Mission mission = new Mission();
            mission.setState(MissionState.LOADING);
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
