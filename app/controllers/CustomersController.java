package controllers;

import com.google.inject.Inject;
import dao.CustomerDao;
import models.Customer;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.customers.index;

import java.util.List;

public class CustomersController extends Controller {

    @Inject
    private CustomerDao customerDao;

    @Transactional
    public Result index() {
        List<Customer> allCustomers = customerDao.findAll();
        return ok(index.render(allCustomers));
    }
}
