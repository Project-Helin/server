package controllers;

import ch.helin.messages.dto.message.missionMessage.ConfirmMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.MissionConfirmType;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.ImprovedTestHelper;
import commons.drone.DroneCommunicationManager;
import dao.DroneDao;
import mappers.MissionMapper;
import models.*;
import org.junit.Test;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;

public class MissionControllerTest extends AbstractIntegrationTest {

    @Inject
    JPAApi jpaApi;

    @Inject
    MissionController missionController;

    @Inject
    DroneDao droneDao;

    @Inject
    ImprovedTestHelper testHelper;

    @Inject
    MissionMapper missionMapper;


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
    public void HandleAcceptedMissionTest() {
        Drone drone = jpaApi.withTransaction((em) -> {

            Customer customer = testHelper.createCustomer();
            Project project = testHelper.createNewProject(testHelper.createNewOrganisation());
            Order order = testHelper.createNewOrderWithThreeMissions(project, customer);
            Drone newDrone = testHelper.createNewDroneForProject(project);
            Mission newMission = testHelper.createNewMission(order);

            newDrone.setCurrentMission(newMission);
            droneDao.persist(newDrone);

            return newDrone;
        });

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

}
