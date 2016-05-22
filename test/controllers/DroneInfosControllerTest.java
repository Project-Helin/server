package controllers;

import ch.helin.messages.dto.DroneInfoDto;
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
    public void testHandleNewDroneInfoMessage() {
        Position position = new Position(47.222645, 8.820594);
        Date now = new Date();

        Drone drone = jpaApi.withTransaction((em) -> {
            return testHelper.createNewDrone(testHelper.createNewOrganisation());
        });

        DroneInfoMessage message = jpaApi.withTransaction(() -> {
            DroneInfoDto droneInfoDto = new DroneInfoDto();
            droneInfoDto.setGpsState(new GpsState());
            droneInfoDto.setClientTime(now);

            droneInfoDto.setPhonePosition(position);
            droneInfoDto.getGpsState().setPosLat(position.getLat());
            droneInfoDto.getGpsState().setPosLon(position.getLon());

            DroneInfoMessage droneInfoMessage = new DroneInfoMessage();
            droneInfoMessage.setDroneInfo(droneInfoDto);
            return droneInfoMessage;
        });

        droneInfosController.onDroneInfoReceived(drone.getId(), message);

        jpaApi.withTransaction(() -> {
            Drone droneFromDB = droneDao.findById(drone.getId());
            DroneInfo droneInfoFromDB = droneFromDB.getDroneInfos().stream().findFirst().get();

            assertThat(droneInfoFromDB.getClientTime().getTime()).isEqualTo(now.getTime());

            Point positionAsPoint = GisHelper.createPoint(position.getLat(), position.getLon());
            assertThat(droneInfoFromDB.getPhonePosition()).isEqualTo(positionAsPoint);
            assertThat(droneInfoFromDB.getDronePosition()).isEqualTo(positionAsPoint);
        });
    }

    @Test
    public void testHandleDroneInfoDuringMission() {
        Date older = new Date(1);
        Date newer = new Date(450000000);

        Drone detachedDrone = jpaApi.withTransaction(em -> {
            return testHelper.createNewDrone(testHelper.createNewOrganisation());
        });

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
        DroneInfoDto olderDroneInfo = new DroneInfoDto();
        olderDroneInfo.setClientTime(older);
        olderDroneInfoMessage.setDroneInfo(olderDroneInfo);

        DroneInfoMessage newerDroneInfoMessage = new DroneInfoMessage();
        DroneInfoDto newerDroneInfo = new DroneInfoDto();
        newerDroneInfo.setClientTime(newer);
        newerDroneInfoMessage.setDroneInfo(newerDroneInfo);

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
