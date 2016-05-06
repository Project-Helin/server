package controllers;

import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.dto.way.Position;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.gis.GisHelper;
import dao.DroneDao;
import dao.MissionsDao;
import models.Drone;
import models.DroneInfo;
import models.Mission;
import models.Organisation;
import org.geolatte.geom.Point;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DroneInfosControllerTest extends AbstractIntegrationTest {

    @Inject
    DroneInfosController droneInfosController;

    @Inject
    DroneDao droneDao;

    @Inject
    MissionsDao missionDao;

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

    @Test
    public void TestHandleDroneInfoDuringMission() {
        Date older = new Date(1);
        Date newer = new Date(2);

        Organisation organisation = testHelper.createNewOrganisation();
        Drone detachedDrone = testHelper.createNewDrone(organisation);

        UUID missionId = jpaApi.withTransaction((em) -> {
            Drone drone = droneDao.findById(detachedDrone.getId());
            Mission mission = new Mission();
            mission.setDrone(drone);
            missionDao.persist(mission);

            drone.setCurrentMission(mission);
            droneDao.persist(drone);

            return mission.getId();
        });

        DroneInfoMessage olderDroneInfoMessage = new DroneInfoMessage();

        olderDroneInfoMessage.setClientTime(older);

        DroneInfoMessage newerDroneInfoMessage = new DroneInfoMessage();

        newerDroneInfoMessage.setClientTime(newer);

        droneInfosController.onDroneInfoReceived(detachedDrone.getId(), olderDroneInfoMessage);
        droneInfosController.onDroneInfoReceived(detachedDrone.getId(), newerDroneInfoMessage);

        jpaApi.withTransaction(() -> {
            Mission missionFromDb = missionDao.findById(missionId);
            DroneInfo firstDroneInfo = missionFromDb.getDroneInfos().stream().findFirst().get();
            DroneInfo secondDroneInfo = missionFromDb.getDroneInfos().stream().reduce((first, second) -> second).get();

            assertThat(firstDroneInfo.getClientTime(), is(newer));
            assertThat(secondDroneInfo.getClientTime(), is(older));
        });
    }

    private Drone createDrone(Organisation organisation) {
        Drone drone = new Drone();
        drone.setName("Best drone ever");
        drone.setToken(UUID.randomUUID());
        drone.setOrganisation(organisation);
        droneDao.persist(drone);
        return drone;
    }
}
