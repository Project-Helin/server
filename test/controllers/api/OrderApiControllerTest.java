package controllers.api;

import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import models.Customer;
import models.Drone;
import models.Order;
import models.Project;
import org.junit.Test;
import play.libs.Json;

import static org.fest.assertions.Assertions.assertThat;


public class OrderApiControllerTest extends AbstractWebServiceIntegrationTest {

    @Inject
    ApiHelper apiHelper;

    @Test
    public void confirmOrderTest() {

        Customer customer = testHelper.createCustomer();
        Project project = testHelper.createNewProject(testHelper.createNewOrganisation());
        Order order = testHelper.createNewOrder(project, customer);
        Drone drone = testHelper.createNewDroneForProject(project);


        Integer ordersBeforeMine = apiHelper.doPost(routes.OrderApiController.confirm(order.getId()), Json.newObject(), Integer.class);

        assertThat(ordersBeforeMine).isEqualTo(0);

        Order order2 = testHelper.createNewOrder(project, customer);

        Integer ordersBeforeMine2 = apiHelper.doPost(routes.OrderApiController.confirm(order2.getId()), Json.newObject(), Integer.class);

        assertThat(ordersBeforeMine2).isEqualTo(1);

        Order order3 = testHelper.createNewOrder(project, customer);

        Integer ordersBeforeMine3 = apiHelper.doPost(routes.OrderApiController.confirm(order3.getId()), Json.newObject(), Integer.class);

        assertThat(ordersBeforeMine3).isEqualTo(2);
    }


}
