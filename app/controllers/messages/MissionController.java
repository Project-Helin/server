package controllers.messages;

import ch.helin.messages.dto.message.missionMessage.*;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import commons.order.MissionDispatchingService;
import dao.DroneDao;
import dao.MissionsDao;
import dao.OrderDao;
import mappers.MissionMapper;
import models.*;
import play.db.jpa.JPAApi;

import java.util.UUID;


public class MissionController {

    @Inject
    private DroneDao droneDao;

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private OrderDao orderDao;

    @Inject
    private JPAApi jpaApi;

    @Inject
    private MissionMapper missionMapper;

    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    @Inject
    private MissionDispatchingService missionDispatchingService;

    public void onConfirmMissionMessageReceived(UUID droneId,
                                                ConfirmMissionMessage missionMessage) {

        jpaApi.withTransaction(() -> {

            Drone drone = droneDao.findById(droneId);
            Mission mission = drone.getCurrentMission();

            boolean missionConfirmed = missionMessage.getMissionConfirmType() == MissionConfirmType.ACCEPT;
            if (missionConfirmed) {

                mission.setState(MissionState.LOADING);

                sendAssignMissionMessage(drone, mission);

                Order order = mission.getOrder();
                order.setState(OrderState.IN_DELIVERY);
                orderDao.persist(order);
            } else {
                mission.setState(MissionState.WAITING_FOR_FREE_DRONE);
                drone.setCurrentMission(null);
            }

            droneDao.persist(drone);
        });
    }

    private void sendAssignMissionMessage(Drone drone, Mission mission) {
        FinalAssignMissionMessage finalAssignMissionMessage = new FinalAssignMissionMessage();
        finalAssignMissionMessage.setMission(missionMapper.convertToMissionDto(mission));

        droneCommunicationManager.sendMessageToDrone(drone.getId(), finalAssignMissionMessage);
    }

    public void onFinishedMissionMessageReceived(UUID droneId, FinishedMissionMessage message) {
        jpaApi.withTransaction(() -> {
            Drone drone = droneDao.findById(droneId);
            Mission mission = drone.getCurrentMission();

            boolean successful = message.getFinishedType() == MissionFinishedType.SUCCESSFUL;
            if (successful) {
                mission.setState(MissionState.DELIVERED);
                drone.setCurrentMission(null);
            } else {
                mission.setState(MissionState.WAITING_FOR_FREE_DRONE);
                drone.setCurrentMission(null);
            }

            missionsDao.persist(mission);
            droneDao.persist(drone);

            missionDispatchingService.tryToDispatchWaitingMissions(mission.getOrder().getProject().getId());

            Order order = mission.getOrder();
            updateOrderState(order);
        });
    }

    private void updateOrderState(Order order) {

        boolean allMissionsDelivered = order.getMissions().stream().allMatch((m) -> m.getState() == MissionState.DELIVERED);
        boolean someMissionsDelivered = order.getMissions().stream().anyMatch((m) -> m.getState() == MissionState.DELIVERED);

        if (allMissionsDelivered) {
            order.setState(OrderState.FINISHED);
        } else if (someMissionsDelivered){
            order.setState(OrderState.PARTIALLY_DELIVERED);
        }

        orderDao.persist(order);
    }

}
