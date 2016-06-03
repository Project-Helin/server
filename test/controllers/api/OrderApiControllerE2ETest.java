package controllers.api;

import ch.helin.messages.dto.OrderDto;
import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.OrderDao;
import mappers.RouteMapper;
import models.Mission;
import models.Order;
import models.Organisation;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

/**
 * We need this test, because it needs browser session
 */
public class OrderApiControllerE2ETest extends AbstractE2ETest {
    private Organisation currentOrganisation;

    @Inject
    private ApiHelper apiHelper;

    @Inject
    private RouteMapper routeMapper;

    @Inject
    private OrderDao orderDao;

    @Before
    public void login() {
        currentOrganisation = doLogin();
    }

    @Test
    public void shouldShowOrder() {
        UUID orderId = jpaApi.withTransaction((em) -> {
            Order newOrder = testHelper.createNewOrderWithThreeMissions(
                    testHelper.createNewProject(currentOrganisation),
                    testHelper.createCustomer()
            );
            return newOrder.getId();
        });

        OrderDto orderDto = apiHelper.doGet(routes.OrderApiController.show(orderId), OrderDto.class, browser);

        jpaApi.withTransaction(() -> {

            Order order = orderDao.findById(orderId);

            assertThat(orderDto.getState()).isEqualTo(order.getState().name());
            assertThat(orderDto.getCustomerName()).isEqualTo(order.getCustomer().getFamilyName());
            assertThat(orderDto.getMissions().size()).isEqualTo(3);

            assertThat(orderDto.getCustomerPosition().getLon())
                .isEqualTo(order.getCustomerPosition().getPosition().getCoordinate(0));
            assertThat(orderDto.getCustomerPosition().getLat())
                .isEqualTo(order.getCustomerPosition().getPosition().getCoordinate(1));

            assertThat(orderDto.getProjectId()).isEqualTo(order.getProject().getId());

            Mission firstMission = order.getMissions().iterator().next();
            assertThat(orderDto.getMissions().get(0).getRoute())
                .isEqualTo(routeMapper.convertToRouteDto(firstMission.getRoute()));
        });

    }


}
