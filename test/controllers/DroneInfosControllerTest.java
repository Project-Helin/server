package controllers;

import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.dto.way.Position;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.gis.GisHelper;
import dao.DroneDao;
import models.Drone;
import models.DroneInfo;
import models.Organisation;
import org.geolatte.geom.Point;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DroneInfosControllerTest extends AbstractIntegrationTest {

    @Inject
    DroneInfosController droneInfosController;

    @Inject
    DroneDao droneDao;

    @Test
    public void TestHandleNewDroneInfoMessage() {

        jpaApi.withTransaction(() -> {
            Organisation organisation = testHelper.createNewOrganisation();
            Drone drone = testHelper.createNewDrone(organisation);

            DroneInfoMessage droneInfoMessage = new DroneInfoMessage();
            droneInfoMessage.setGpsState(new GpsState());

            Date now = new Date();
            droneInfoMessage.setClientTime(now);
            Position position = new Position(47.222645, 8.820594);
            droneInfoMessage.setPhonePosition(position);
            droneInfoMessage.getGpsState().setPosLat(position.getLat());
            droneInfoMessage.getGpsState().setPosLon(position.getLon());

            droneInfosController.onDroneInfoReceived(drone.getId(), droneInfoMessage);
            jpaApi.em().flush();

            Drone droneFromDB = droneDao.findById(drone.getId());
            DroneInfo droneInfoFromDB = droneFromDB.getDroneInfos().stream().findFirst().get();

            assertThat(droneInfoFromDB.getClientTime(), is(now));

            Point positionAsPoint = GisHelper.createPoint(position.getLat(), position.getLon());
            assertThat(droneInfoFromDB.getPhonePosition(), is(positionAsPoint));
            assertThat(droneInfoFromDB.getDronePosition(), is(positionAsPoint));
        });
    }

}
