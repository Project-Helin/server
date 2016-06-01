package controllers.api;

import com.google.gson.Gson;
import com.google.inject.Inject;
import dao.CustomerDao;
import dto.api.CustomerApiDto;
import models.Customer;
import org.slf4j.Logger;
import play.db.jpa.JPAApi;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

public class CustomerApiController extends Controller {
    private static final Logger logger = getLogger(CustomerApiController.class);

    @Inject
    private CustomerDao customerDao;

    @Inject
    private JPAApi jpaApi;

    public Result show(UUID customerId) {
        logger.info("Find customer by id {}", customerId);
        Customer found = jpaApi.withTransaction(em -> customerDao.findById(customerId));

        CustomerApiDto customerApiDto = new CustomerApiDto();
        customerApiDto.setFamilyName(found.getFamilyName());
        customerApiDto.setGivenName(found.getGivenName());
        customerApiDto.setEmail(found.getEmail());
        customerApiDto.setId(found.getIdAsString());

        return ok(Json.toJson(found));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result save() {
        String jsonNode = request().body().asJson().toString();
        CustomerApiDto customerApiDto = parseJsonOrNull(jsonNode);

        if (customerApiDto == null) {
            logger.debug("Send wrong request back, because invalid json: {}", jsonNode);
            return forbidden("Wrong request");
        }

        try {
            CustomerApiDto routeDto = createCustomer(customerApiDto);
            return ok(Json.toJson(routeDto));
        } catch (RuntimeException e) {
            logger.warn("Failed to process input: {}", jsonNode);
            throw e;
        }
    }

    private CustomerApiDto createCustomer(CustomerApiDto customerApiDto) {
        Customer customer = new Customer();
        customer.setFamilyName(customerApiDto.getFamilyName());
        customer.setGivenName(customerApiDto.getGivenName());
        customer.setEmail(customerApiDto.getEmail());

        jpaApi.withTransaction(() -> {
            customerDao.persist(customer);
        });

        customerApiDto.setId(customer.getId().toString());
        return customerApiDto;
    }

    private CustomerApiDto parseJsonOrNull(String jsonNode) {
        try {
            return new Gson().fromJson(jsonNode, CustomerApiDto.class);
        } catch (Exception e) {
            logger.warn("Failed to parse json {}", jsonNode, e);
            return null;
        }
    }


}
