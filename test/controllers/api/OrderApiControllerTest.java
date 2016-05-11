package controllers.api;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import commons.drone.DroneCommunicationManager;
import dao.OrderDao;
import mappers.MissionMapper;
import mappers.OrderProductsMapper;
import models.*;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;

import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static play.inject.Bindings.bind;


public class OrderApiControllerTest extends AbstractWebServiceIntegrationTest {

    @Inject
    private ApiHelper apiHelper;

    @Inject
    private OrderDao orderDao;

    @Inject
    private MissionMapper missionMapper;

    @Inject
    private OrderProductsMapper orderProductsMapper;

    private DroneCommunicationManager droneCommunicationManager;


    @Override
    protected Application provideApplication() {

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
    public void confirmOrderTest() {

        Customer customer = testHelper.createCustomer();
        Project project = testHelper.createNewProject(testHelper.createNewOrganisation());
        Order order = testHelper.createNewOrderWithThreeMissions(project, customer);
        Drone drone = testHelper.createNewDroneForProject(project);

        apiHelper.doPost(OrderApiController.confirm(order.getId()), Json.newObject());

        jpaApi.withTransaction(() -> {
            Order loadedOrder = orderDao.findById(order.getId());

            assertThat(loadedOrder.getState()).isEqualTo(OrderState.IN_PROGRESS);

            Iterator<Mission> missionIterator = loadedOrder.getMissions().iterator();
            Mission firstMission = missionIterator.next();
            Mission secondMission = missionIterator.next();
            Mission thirdMission = missionIterator.next();

            assertThat(firstMission.getState()).isEqualTo(MissionState.WAITING_FOR_DRONE_CONFIRMATION);
            assertThat(secondMission.getState()).isEqualTo(MissionState.WAITING_FOR_FREE_DRONE);
            assertThat(thirdMission.getState()).isEqualTo(MissionState.WAITING_FOR_FREE_DRONE);

            assertThat(firstMission.getDrone()).isEqualTo(drone);
            assertThat(secondMission.getDrone()).isNull();
            assertThat(thirdMission.getDrone()).isNull();

            AssignMissionMessage expectedMessage = new AssignMissionMessage();
            expectedMessage.setMission(missionMapper.convertToMissionDto(firstMission));

            verify(droneCommunicationManager, times(1)).sendMessageToDrone(drone.getId(), expectedMessage);
        });

    }


}
