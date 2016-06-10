package controllers;

import com.google.inject.Inject;
import service.SessionHelper;
import dao.CustomerDao;
import models.Customer;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.customers.index;

import java.util.List;

public class CustomersController extends Controller {

    @Inject
    private CustomerDao customerDao;

    @Inject
    private SessionHelper sessionHelper;

    @Transactional
    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        List<Customer> allCustomers = customerDao.findCustomerByOrganisation(
            sessionHelper.getOrganisation(session())
        );
        return ok(index.render(allCustomers));
    }
}
