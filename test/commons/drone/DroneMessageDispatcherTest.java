package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.message.stateMessage.DroneStateMessage;
import ch.helin.messages.dto.state.DroneState;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import controllers.DroneInfoController;
import models.Drone;
import models.Organisation;
import models.User;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static play.inject.Bindings.bind;

public class DroneMessageDispatcherTest extends AbstractIntegrationTest {

    private DroneInfoController droneStateController;

    @Inject
    DroneMessageDispatcher droneMessageDispatcher;

    @Inject
    JsonBasedMessageConverter messageConverter;

    @Override
    protected Application provideApplication() {

        this.droneStateController = mock(DroneInfoController.class);

        return new GuiceApplicationBuilder()
                .configure("driver", "org.postgresql.Driver")
                .configure("url", "jdbc:postgresql://localhost:5455/test")
                .configure("username", "test")
                .configure("password", "test")
                .overrides(bind(DroneInfoController.class).toInstance(droneStateController))
                .build();
    }

    @Test
    public void testDroneStateMessage() {
        String password = "password243";

        User userWithOrganisation = testHelper.createUserWithOrganisation(password);
        Organisation organisation = userWithOrganisation.getOrganisations().stream().findFirst().get();

        Drone drone = testHelper.createNewDrone(organisation);

        DroneState droneState = testHelper.getDroneState();

        DroneStateMessage droneStateMessage = new DroneStateMessage();
        droneStateMessage.setDroneState(droneState);

        String messageAsJSON = messageConverter.parseMessageToString(droneStateMessage);
        droneMessageDispatcher.dispatchMessage(drone.getId(), messageAsJSON);

        verify(droneStateController, times(1)).onDroneInfoReceived(drone.getId(), droneState);
    }


}
