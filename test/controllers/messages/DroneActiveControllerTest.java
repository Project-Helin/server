package controllers.messages;

import ch.helin.messages.dto.message.DroneActiveState;
import ch.helin.messages.dto.message.DroneActiveStateMessage;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.drone.DroneCommunicationManager;
import dao.DroneDao;
import models.Drone;
import org.junit.Test;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;

public class DroneActiveControllerTest extends AbstractIntegrationTest{

    @Inject
    private DroneActiveController droneActiveController;

    @Inject
    private DroneDao droneDao;

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

}