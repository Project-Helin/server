package controllers.api;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.way.Position;
import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import commons.ImprovedTestHelper;
import commons.drone.DroneCommunicationManager;
import dao.OrderDao;
import dao.ProjectsDao;
import dto.api.OrderApiDto;
import dto.api.OrderProductApiDto;
import mappers.MissionMapper;
import models.*;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;

import java.util.*;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;


public class OrderApiControllerIntegrationTest extends AbstractWebServiceIntegrationTest {

    @Inject
    private ApiHelper apiHelper;

    @Inject
    private OrderDao orderDao;

    @Inject
    private MissionMapper missionMapper;

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
            Organisation organisation = testHelper.createNewOrganisation();
            Project project = testHelper.createNewProjectWithTwoZones(organisation);
            Product product = testHelper.createProduct(organisation);
            projectsDao.persist(project);

            return new OrderApiDto()
                .setCustomerPosition(new Position(10.03, 30.200))
                .setDisplayName("Batman")
                .setEmail("batman@wayneenterprise.com")
                .setProjectId(project.getIdAsString())
                .setOrderProducts(Arrays.asList(
                    new OrderProductApiDto()
                        .setProductId(product.getIdAsString())
                        .setAmount(1)
                ));
        });

        // do request
        apiHelper.doPost(routes.OrderApiController.create(), orderToSent);

        jpaApi.withTransaction(() -> {

            // verify
            List<Order> all = orderDao.findAll();
            assertThat(all).hasSize(1);

            // should save the customer
            Customer customer = all.get(0).getCustomer();
            assertThat(customer.getDisplayName()).isEqualTo("Batman");
            assertThat(customer.getEmail()).isEqualTo("batman@wayneenterprise.com");

            // should has one mission
            List<Mission> missions = getFirstMissionSortedByAmount(all);
            assertThat(missions).hasSize(1);

            Mission first = missions.get(0);
            assertThat(first.getOrderProduct().getProduct().getIdAsString())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getProductId());
            assertThat(first.getOrderProduct().getAmount())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getAmount());

            assertThat(first.getRoute()).isNotNull();
            assertThat(first.getRoute().getWayPoints()).isNotEmpty();
            // TODO Kiru verify waypoint have action fly ( can be done if route is fixed )

        });
    }

    @Test
    public void shouldNotSplitBecauseOrderAmountIsSmallerThanMaxAmount() {
        OrderApiDto orderToSent = jpaApi.withTransaction((em) -> {

            int maxItemsPerDrone = 5;
            Organisation organisation = testHelper.createNewOrganisation();
            Project project = testHelper.createNewProjectWithTwoZones(organisation);
            Product product = testHelper.createProduct(organisation, maxItemsPerDrone);
            projectsDao.persist(project);

            return new OrderApiDto()
                .setCustomerPosition(new Position(10.03, 30.200))
                .setDisplayName("Batman")
                .setEmail("batman@wayneenterprise.com")
                .setProjectId(project.getIdAsString())
                .setOrderProducts(Arrays.asList(
                    new OrderProductApiDto()
                        .setProductId(product.getIdAsString())
                        .setAmount(3) // <= we order 3 items
                ));
        });

        // do request
        apiHelper.doPost(routes.OrderApiController.create(), orderToSent);

        jpaApi.withTransaction(() -> {
            // verify
            List<Order> all = orderDao.findAll();
            assertThat(all).hasSize(1);

            // should has one mission
            List<Mission> missions = getFirstMissionSortedByAmount(all);
            assertThat(missions).hasSize(1);

            Mission first = missions.get(0);
            assertThat(first.getOrderProduct().getProduct().getIdAsString())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getProductId());
            assertThat(first.getOrderProduct().getAmount()).isEqualTo(3);
        });
    }


    @Test
    public void shouldSplitOrderIntoTwoExactMissions() {
        OrderApiDto orderToSent = jpaApi.withTransaction((em) -> {

            int maxItemsPerDrone = 5;
            Organisation organisation = testHelper.createNewOrganisation();
            Project project = testHelper.createNewProjectWithTwoZones(organisation);
            Product product = testHelper.createProduct(organisation, maxItemsPerDrone);
            projectsDao.persist(project);

            return new OrderApiDto()
                .setCustomerPosition(new Position(10.03, 30.200))
                .setDisplayName("Batman")
                .setEmail("batman@wayneenterprise.com")
                .setProjectId(project.getIdAsString())
                .setOrderProducts(Arrays.asList(
                    new OrderProductApiDto()
                        .setProductId(product.getIdAsString())
                        .setAmount(10) // <= we order 10 items
                ));
        });

        // do request
        apiHelper.doPost(routes.OrderApiController.create(), orderToSent);

        jpaApi.withTransaction(() -> {
            // verify
            List<Order> all = orderDao.findAll();
            assertThat(all).hasSize(1);

            Order firstOrder = all.get(0);

            // should has one mission
            Set<Mission> missions = firstOrder.getMissions();
            assertThat(missions).hasSize(2);

            Iterator<Mission> iterator = missions.iterator();

            Mission first = iterator.next();
            assertThat(first.getOrderProduct().getProduct().getIdAsString())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getProductId());
            assertThat(first.getOrderProduct().getAmount()).isEqualTo(5);

            Mission second = iterator.next();
            assertThat(second.getOrderProduct().getProduct().getIdAsString())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getProductId());
            assertThat(second.getOrderProduct().getAmount()).isEqualTo(5);
        });
    }

    @Test
    public void shouldSplitOrderIntoThreeExactMissionsNotEven() {
        OrderApiDto orderToSent = jpaApi.withTransaction((em) -> {

            int maxItemsPerDrone = 5;
            Organisation organisation = testHelper.createNewOrganisation();
            Project project = testHelper.createNewProjectWithTwoZones(organisation);
            Product product = testHelper.createProduct(organisation, maxItemsPerDrone);
            projectsDao.persist(project);

            return new OrderApiDto()
                .setCustomerPosition(new Position(10.03, 30.200))
                .setDisplayName("Batman")
                .setEmail("batman@wayneenterprise.com")
                .setProjectId(project.getIdAsString())
                .setOrderProducts(Arrays.asList(
                    new OrderProductApiDto()
                        .setProductId(product.getIdAsString())
                        .setAmount(13) // <= we order 13 items
                ));
        });

        // do request
        apiHelper.doPost(routes.OrderApiController.create(), orderToSent);

        jpaApi.withTransaction(() -> {
            // verify
            List<Order> all = orderDao.findAll();
            assertThat(all).hasSize(1);

            // should has one mission
            List<Mission> missions = getFirstMissionSortedByAmount(all);
            assertThat(missions).hasSize(3);

            Mission first = missions.get(0);
            assertThat(first.getOrderProduct().getProduct().getIdAsString())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getProductId());
            assertThat(first.getOrderProduct().getAmount()).isEqualTo(3);

            Mission second = missions.get(1);
            assertThat(second.getOrderProduct().getProduct().getIdAsString())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getProductId());
            assertThat(second.getOrderProduct().getAmount()).isEqualTo(5);

            Mission third = missions.get(2);
            assertThat(third.getOrderProduct().getProduct().getIdAsString())
                .isEqualTo(orderToSent.getOrderProducts().get(0).getProductId());
            assertThat(third.getOrderProduct().getAmount()).isEqualTo(5);
        });
    }

    @Test
    public void confirmOrderTest() {
        final Drone[] drone = new Drone[1];

        Order order = jpaApi.withTransaction((em) -> {
            Customer customer = testHelper.createCustomer();
            Project project = testHelper.createNewProject(testHelper.createNewOrganisation());
            drone[0] = testHelper.createNewDroneForProject(project);

            return testHelper.createNewOrderWithThreeMissions(project, customer);
        });

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

            assertThat(firstMission.getDrone()).isEqualTo(drone[0]);
            assertThat(secondMission.getDrone()).isNull();
            assertThat(thirdMission.getDrone()).isNull();

            AssignMissionMessage expectedMessage = new AssignMissionMessage();
            expectedMessage.setMission(missionMapper.convertToMissionDto(firstMission));

            verify(droneCommunicationManager, times(1)).sendMessageToDrone(drone[0].getId(), expectedMessage);
        });
    }


    private List<Mission> getFirstMissionSortedByAmount(List<Order> all) {
        return all.get(0)
            .getMissions()
            .stream()
            .sorted(Comparator.comparing((e) -> e.getOrderProduct().getAmount()))
            .collect(Collectors.toList());
    }
}
