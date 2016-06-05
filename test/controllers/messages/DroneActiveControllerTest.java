package controllers.messages;

import ch.helin.messages.dto.message.DroneActiveState;
import ch.helin.messages.dto.message.DroneActiveStateMessage;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.drone.DroneCommunicationManager;
import commons.order.MissionDispatchingService;
import dao.DroneDao;
import dao.MissionsDao;
import models.*;
import org.junit.Ignore;
import org.junit.Test;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;

public class DroneActiveControllerTest extends AbstractIntegrationTest{

    @Inject
    private DroneActiveController droneActiveController;

    @Inject
    private DroneDao droneDao;

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Inject
    private JPAApi jpaApi;

    @Override
    protected play.Application provideApplication() {

        DroneCommunicationManager droneCommunicationManager = mock(DroneCommunicationManager.class);

        return new GuiceApplicationBuilder()
                .overrides(bind(DroneCommunicationManager.class).toInstance(droneCommunicationManager))
                .build();
    }

    @Test
    public void testHandleActiveUpdate() {
        Drone drone = jpaApi.withTransaction((em) -> {
            return testHelper.createNewDrone(testHelper.createNewOrganisation());
        });

        DroneActiveState droneActiveState = new DroneActiveState();
        droneActiveState.setActive(false);

        DroneActiveStateMessage droneActiveStateMessage = new DroneActiveStateMessage();
        droneActiveStateMessage.setDroneActiveState(droneActiveState);

        droneActiveController.onDroneActiveStateReceived(drone.getId(), droneActiveStateMessage);

        jpaApi.withTransaction(() -> {
            Drone droneFromDB = droneDao.findById(drone.getId());
            assertThat(droneFromDB.getIsActive().equals(false));
        });

    }

    @Test
    public void testHandleInactiveWithMission(){
        Drone drone = jpaApi.withTransaction((em) -> {
            Project newProject = testHelper.createNewProject(testHelper.createNewOrganisation());
            Drone newDroneForProject = testHelper.createNewDroneForProject(newProject, true);

            Order testOrder = testHelper.createNewOrder(newProject, testHelper.createCustomer());
            Mission newMission = testHelper.createNewMission(testOrder);
            newMission.setState(MissionState.WAITING_FOR_FREE_DRONE);

            missionDispatchingService.tryToDispatchWaitingMissions(newProject.getId());

            return newDroneForProject;
        });

        Mission mission = jpaApi.withTransaction((em) -> {
            Drone droneFromDB = droneDao.findById(drone.getId());
            return droneFromDB.getCurrentMission();
        });

        System.out.println(drone);

        UUID currentMissionId = mission.getId();

        DroneActiveState droneActiveState = new DroneActiveState();
        droneActiveState.setActive(false);

        DroneActiveStateMessage droneActiveStateMessage = new DroneActiveStateMessage();
        droneActiveStateMessage.setDroneActiveState(droneActiveState);

        droneActiveController.onDroneActiveStateReceived(drone.getId(), droneActiveStateMessage);

        jpaApi.withTransaction(() -> {
            Drone droneFromDB = droneDao.findById(drone.getId());
            assertThat(droneFromDB.getIsActive().equals(false));
            assertThat(droneFromDB.getCurrentMission()).isNull();

            Mission currentMissionFromDB = missionsDao.findById(currentMissionId);
            assertThat(currentMissionFromDB.getDrone()).isNull();
            assertThat(currentMissionFromDB.getState()).isEqualTo(MissionState.WAITING_FOR_FREE_DRONE);
        });


    }



}