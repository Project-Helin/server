package controllers;

import ch.helin.messages.dto.message.DroneDtoMessage;
import ch.helin.messages.dto.message.missionMessage.*;
import com.google.inject.Inject;
import service.AbstractIntegrationTest;
import service.drone.DroneCommunicationManager;
import controllers.messages.MissionController;
import dao.DroneDao;
import dao.MissionsDao;
import mappers.DroneMapper;
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

    @Inject
    private DroneMapper droneMapper;


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
        Drone drone = createDroneWithAssignedMissionInTransaction();

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
        Drone drone = createDroneWithAssignedMissionInTransaction();

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
    public void onRejectConfirmMissionMessageReceivedTest() {
        UUID droneId = createDroneWithAssignedMissionInTransaction().getId();

        jpaApi.withTransaction(() -> {
            ConfirmMissionMessage confirmMissionMessage = new ConfirmMissionMessage();
            confirmMissionMessage.setMissionConfirmType(MissionConfirmType.REJECT);

            missionController.onConfirmMissionMessageReceived(droneId, confirmMissionMessage);
        });

        jpaApi.withTransaction(() ->  {
            Drone droneFromDb = droneDao.findById(droneId);

            DroneDtoMessage droneDtoMessage = new DroneDtoMessage();
            droneDtoMessage.setDroneDto(droneMapper.getDroneDto(droneFromDb));

            verify(droneCommunicationManager, times(1)).sendMessageToDrone(droneId, droneDtoMessage);
        });
    }

    public Drone createDroneWithAssignedMissionInTransaction(){
        Drone drone = jpaApi.withTransaction((em) -> {
            return testHelper.createDroneWithAssignedMission();
        });

        return drone;
    }
}
