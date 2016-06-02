package controllers;

import ch.helin.messages.dto.message.missionMessage.*;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.drone.DroneCommunicationManager;
import controllers.messages.MissionController;
import dao.DroneDao;
import dao.MissionsDao;
import mappers.MissionMapper;
import models.*;
import org.junit.Test;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;

public class MissionControllerTest extends AbstractIntegrationTest {

    @Inject
    private JPAApi jpaApi;

    @Inject
    private MissionController missionController;

    @Inject
    private DroneDao droneDao;

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private MissionMapper missionMapper;


    private DroneCommunicationManager droneCommunicationManager;


    @Override
    protected play.Application provideApplication() {

        this.droneCommunicationManager = mock(DroneCommunicationManager.class);

        return new GuiceApplicationBuilder()
                .configure("driver", "org.postgresql.Driver")
                .configure("url", "jdbc:postgresql://localhost:5455/test")
                .configure("username", "test")
                .configure("password", "test")
                .overrides(bind(DroneCommunicationManager.class).toInstance(droneCommunicationManager))
                .build();
    }

    @Test
    public void onConfirmMissionMessageReceivedTest() {
        Drone drone = createDroneWithAssignedMission();

        jpaApi.withTransaction(() -> {

            ConfirmMissionMessage confirmMissionMessage = new ConfirmMissionMessage();
            confirmMissionMessage.setMissionConfirmType(MissionConfirmType.ACCEPT);

            missionController.onConfirmMissionMessageReceived(drone.getId(), confirmMissionMessage);

            Drone droneFromDB = droneDao.findById(drone.getId());
            Mission missionFromDB = droneFromDB.getCurrentMission();

            assertThat(missionFromDB.getState()).isEqualTo(MissionState.LOADING);

            FinalAssignMissionMessage expectedMessage = new FinalAssignMissionMessage();
            expectedMessage.setMission(missionMapper.convertToMissionDto(missionFromDB));

            verify(droneCommunicationManager, times(1)).sendMessageToDrone(drone.getId(), expectedMessage);
        });
    }

    @Test
    public void onSuccessfulFinishedMissionMessageReceivedTest() {
        Drone drone = createDroneWithAssignedMission();

        UUID missionId = jpaApi.withTransaction((em) -> {
            FinishedMissionMessage finishedMissionMessage = new FinishedMissionMessage();
            finishedMissionMessage.setFinishedType(MissionFinishedType.SUCCESSFUL);

            missionController.onFinishedMissionMessageReceived(drone.getId(), finishedMissionMessage);

            return drone.getCurrentMission().getId();
        });

        jpaApi.withTransaction(() -> {
            Drone droneFromDB = droneDao.findById(drone.getId());
            Mission missionFromDB = missionsDao.findById(missionId);

            assertThat(missionFromDB.getState()).isEqualTo(MissionState.DELIVERED);
            assertThat(droneFromDB.getCurrentMission()).isNull();

        });
    }

    @Test
    public void onFailedFinishedMissionMessageReceivedTest() {
        Drone drone = createDroneWithAssignedMission();

        UUID missionId = jpaApi.withTransaction((em) -> {
            FinishedMissionMessage finishedMissionMessage = new FinishedMissionMessage();
            finishedMissionMessage.setFinishedType(MissionFinishedType.FAILED);

            missionController.onFinishedMissionMessageReceived(drone.getId(), finishedMissionMessage);

            return drone.getCurrentMission().getId();
        });

        jpaApi.withTransaction(() -> {
            //should reassign failed mission
            Drone droneFromDB = droneDao.findById(drone.getId());
            Mission missionFromDB = missionsDao.findById(missionId);

            assertThat(missionFromDB.getState()).isEqualTo(MissionState.WAITING_FOR_DRONE_CONFIRMATION);
            assertThat(droneFromDB.getCurrentMission()).isEqualTo(missionFromDB);
        });
    }

    private Drone createDroneWithAssignedMission() {
        return jpaApi.withTransaction((em) -> {

            Customer customer = testHelper.createCustomer();
            Project project = testHelper.createNewProject(testHelper.createNewOrganisation());
            Order order = testHelper.createNewOrderWithThreeMissions(project, customer);
            Drone newDrone = testHelper.createNewDroneForProject(project, true);
            Mission newMission = testHelper.createNewMission(order);

            newDrone.setCurrentMission(newMission);
            droneDao.persist(newDrone);

            return newDrone;
        });
    }
}
