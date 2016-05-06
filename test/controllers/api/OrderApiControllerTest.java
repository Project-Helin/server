package controllers.api;

import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import models.Customer;
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

        Integer ordersBeforeMine = apiHelper.doPost(routes.OrderApiController.confirm(order.getId()), Json.newObject(), Integer.class);

        assertThat(ordersBeforeMine).isEqualTo(0);

        Customer customer2 = testHelper.createCustomer();
        Project project2 = testHelper.createNewProject(testHelper.createNewOrganisation());
        Order order2 = testHelper.createNewOrder(project2, customer2);

        Integer ordersBeforeMine2 = apiHelper.doPost(routes.OrderApiController.confirm(order2.getId()), Json.newObject(), Integer.class);

        assertThat(ordersBeforeMine).isEqualTo(1);
    }


}
