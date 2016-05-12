package controllers;

import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.dto.way.Position;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.gis.GisHelper;
import dao.DroneDao;
import dao.MissionsDao;
import dao.OrderDao;
import models.*;
import org.geolatte.geom.Point;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;


public class DroneInfosControllerTest extends AbstractIntegrationTest {

    @Inject
    private DroneInfosController droneInfosController;

    @Inject
    private DroneDao droneDao;

    @Inject
    private MissionsDao missionDao;

    @Inject
    private OrderDao orderDao;

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

            assertThat(droneInfoFromDB.getClientTime().getTime()).isEqualTo(now.getTime());

            Point positionAsPoint = GisHelper.createPoint(position.getLat(), position.getLon());
            assertThat(droneInfoFromDB.getPhonePosition()).isEqualTo(positionAsPoint);
            assertThat(droneInfoFromDB.getDronePosition()).isEqualTo(positionAsPoint);
        });
    }

    @Test
    public void TestHandleDroneInfoDuringMission() {
        Date older = new Date(1);
        Date newer = new Date(450000000);

        Organisation organisation = testHelper.createNewOrganisation();
        Drone detachedDrone = testHelper.createNewDrone(organisation);

        UUID missionId = jpaApi.withTransaction((em) -> {
            Drone drone = droneDao.findById(detachedDrone.getId());

            Mission mission = new Mission();
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setAmount(1);
            orderProduct.setTotalPrice(2.0);
            mission.setOrderProduct(orderProduct);

            Order order = new Order();
            orderDao.persist(order);
            mission.setOrder(order);

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

            assertThat(firstDroneInfo.getClientTime().getTime()).isEqualTo(newer.getTime());
            assertThat(secondDroneInfo.getClientTime().getTime()).isEqualTo(older.getTime());
        });
    }
}
