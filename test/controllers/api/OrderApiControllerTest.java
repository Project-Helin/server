package controllers.api;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.way.Position;
import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import commons.ImprovedTestHelper;
import commons.drone.DroneCommunicationManager;
import commons.gis.GisHelper;
import dao.OrderDao;
import dao.ProjectsDao;
import dto.api.OrderApiDto;
import dto.api.OrderProductApiDto;
import mappers.MissionMapper;
import mappers.OrderProductsMapper;
import models.*;
import org.junit.Test;
import play.Application;
import play.db.jpa.Transactional;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;

import java.util.Arrays;
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

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private ImprovedTestHelper testHelper;

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
    public void shouldCreateNewOrderForOneProduct() {
        OrderApiDto orderToSent = jpaApi.withTransaction((em) -> {
            Organisation organisation;
            Project project = testHelper.createNewProject(
                organisation = testHelper.createNewOrganisation(),
                testHelper.createUnsavedZone(
                    "Loading Zone",
                    ZoneType.LoadingZone,
                    testHelper.createSamplePolygon()
                ),
                testHelper.createUnsavedZone(
                    "Delivery Zone",
                    ZoneType.DeliveryZone,
                    testHelper.createSamplePolygon()
                )
            );
            Product product = testHelper.createProduct(organisation);
            projectsDao.persist(project);

            OrderApiDto orderApiDto = new OrderApiDto()
                .setCustomerPosition(new Position(10.03, 30.200))
                .setDisplayName("Batman")
                .setEmail("batman@wayneenterprise.com")
                .setOrderProducts(Arrays.asList(
                    new OrderProductApiDto()
                        .setProjectId(project.getIdAsString())
                        .setProductId(product.getIdAsString())
                        .setAmount(10)
                ));

            return orderApiDto;
        });

        apiHelper.doPost(routes.OrderApiController.create(), orderToSent);
    }


    @Test
    public void confirmOrderTest() {

        Customer customer = testHelper.createCustomer();
        Project project = testHelper.createNewProject(testHelper.createNewOrganisation());
        Order order = testHelper.createNewOrderWithThreeMissions(project, customer);
        Drone drone = testHelper.createNewDroneForProject(project);

        apiHelper.doPost(routes.OrderApiController.confirm(order.getId()), Json.newObject());

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
