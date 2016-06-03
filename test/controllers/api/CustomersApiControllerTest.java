package controllers.api;

import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import dao.CustomerDao;
import dto.api.CustomerApiDto;
import models.Customer;
import org.junit.Test;
import play.libs.Json;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class CustomersApiControllerTest extends AbstractWebServiceIntegrationTest {

    @Inject
    private ApiHelper apiHelper;

    @Inject
    private CustomerDao customerDao;

    @Test
    public void shouldCreateCustomer() {
        CustomerApiDto customerApiDto = new CustomerApiDto();
        customerApiDto.setEmail("batman@wayneenterprise");
        customerApiDto.setFamilyName("Wayne");
        customerApiDto.setGivenName("Bruce");

        CustomerApiDto returnedCustomer = apiHelper.doPost(routes.CustomerApiController.save(), Json.toJson(customerApiDto), CustomerApiDto.class);

        assertThat(returnedCustomer.getId()).isNotNull();
        assertThat(returnedCustomer.getFamilyName()).isEqualTo(customerApiDto.getFamilyName());
        assertThat(returnedCustomer.getGivenName()).isEqualTo(customerApiDto.getGivenName());

        // check if it in db
        Customer found = jpaApi.withTransaction(() -> customerDao.findById(UUID.fromString(returnedCustomer.getId())));
        assertThat(found.getId()).isNotNull();
        assertThat(found.getFamilyName()).isEqualTo(customerApiDto.getFamilyName());
        assertThat(found.getGivenName()).isEqualTo(customerApiDto.getGivenName());
    }

    @Test
    public void shouldShowCustomer() {
        Customer customerInDb = jpaApi.withTransaction(em -> testHelper.createCustomer("Bruce", "Wayne"));

        CustomerApiDto returnedCustomer = apiHelper.doGet(routes.CustomerApiController.show(customerInDb.getId()), CustomerApiDto.class);

        assertThat(returnedCustomer.getId()).isEqualTo(customerInDb.getIdAsString());
        assertThat(returnedCustomer.getFamilyName()).isEqualTo(customerInDb.getFamilyName());
        assertThat(returnedCustomer.getGivenName()).isEqualTo(customerInDb.getGivenName());
    }
}