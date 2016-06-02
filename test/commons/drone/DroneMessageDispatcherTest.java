package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.DroneInfoDto;
import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.state.DroneState;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import controllers.messages.DroneInfosController;
import models.Drone;
import models.Organisation;
import models.User;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;

public class DroneMessageDispatcherTest extends AbstractIntegrationTest {

    private DroneInfosController droneStateController;

    @Inject
    DroneMessageDispatcher droneMessageDispatcher;

    @Inject
    JsonBasedMessageConverter messageConverter;

    @Override
    protected Application provideApplication() {

        this.droneStateController = mock(DroneInfosController.class);

        return new GuiceApplicationBuilder()
                .configure("driver", "org.postgresql.Driver")
                .configure("url", "jdbc:postgresql://localhost:5455/test")
                .configure("username", "test")
                .configure("password", "test")
                .overrides(bind(DroneInfosController.class).toInstance(droneStateController))
                .build();
    }

    @Test
    public void testDroneStateMessage() {
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


}
