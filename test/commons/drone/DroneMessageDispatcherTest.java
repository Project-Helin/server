package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.DroneInfoDto;
import ch.helin.messages.dto.message.DroneDto;
import ch.helin.messages.dto.message.DroneDtoMessage;
import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.message.missionMessage.ConfirmMissionMessage;
import ch.helin.messages.dto.message.missionMessage.MissionConfirmType;
import ch.helin.messages.dto.state.DroneState;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import controllers.messages.DroneInfosController;
import dao.DroneDao;
import mappers.DroneMapper;
import models.*;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;

public class DroneMessageDispatcherTest extends AbstractIntegrationTest {

    private DroneInfosController droneStateController;

    @Inject
    DroneMessageDispatcher droneMessageDispatcher;

    @Inject
    JsonBasedMessageConverter messageConverter;

    @Inject
    DroneMapper droneMapper;

    @Inject
    DroneDao droneDao;

    DroneCommunicationManager droneCommunicationManager;

    @Override
    protected Application provideApplication() {

        this.droneCommunicationManager = mock(DroneCommunicationManager.class);

        this.droneStateController = mock(DroneInfosController.class);

        return new GuiceApplicationBuilder()
                .configure("driver", "org.postgresql.Driver")
                .configure("url", "jdbc:postgresql://localhost:5455/test")
                .configure("username", "test")
                .configure("password", "test")
                .overrides(bind(DroneInfosController.class).toInstance(droneStateController))
                .overrides(bind(DroneCommunicationManager.class).toInstance(droneCommunicationManager))
                .build();
    }

    @Test
    public void droneInfoMessageTest() {
        final Drone[] drone = new Drone[1];

        DroneInfoMessage droneInfoMessage = jpaApi.withTransaction((em) -> {
            String password = "password243";

            User userWithOrganisation = testHelper.createUserWithOrganisation(password);
            Organisation organisation = userWithOrganisation.getOrganisations().stream().findFirst().get();

            drone[0] = testHelper.createNewDrone(organisation);

            DroneState droneState = testHelper.getDroneState();

            DroneInfoMessage infoMessage = new DroneInfoMessage();
            DroneInfoDto droneInfoDto = new DroneInfoDto();
            droneInfoDto.setDroneState(droneState);
            infoMessage.setDroneInfo(droneInfoDto);

            return infoMessage;
        });


        String messageAsJSON = messageConverter.parseMessageToString(droneInfoMessage);
        droneMessageDispatcher.dispatchMessageToController(drone[0].getId(), messageAsJSON);

        verify(droneStateController, times(1)).onDroneInfoReceived(drone[0].getId(), droneInfoMessage);
    }

    @Test
    public void missionRejectNotifyDroneInactiveTest(){
        UUID droneId = jpaApi.withTransaction((em) -> {
            Drone drone = testHelper.createDroneWithAssignedMission();
            return drone.getId();
        });

        jpaApi.withTransaction(() -> {

            ConfirmMissionMessage confirmMissionMessage = new ConfirmMissionMessage();
            confirmMissionMessage.setMissionConfirmType(MissionConfirmType.REJECT);
            JsonBasedMessageConverter jsonBasedMessageConverter = new JsonBasedMessageConverter();
            String rejectMessageString = jsonBasedMessageConverter.parseMessageToString(confirmMissionMessage);

            droneMessageDispatcher.dispatchMessageToController(droneId, rejectMessageString);

            Drone droneFromDb = droneDao.findById(droneId);

            DroneDtoMessage droneDtoMessage = new DroneDtoMessage();
            droneDtoMessage.setDroneDto(droneMapper.getDroneDto(droneFromDb));

            verify(droneCommunicationManager).sendMessageToDrone(droneId, droneDtoMessage);
        });

    }

}
