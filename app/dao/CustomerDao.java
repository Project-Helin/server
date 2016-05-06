package dao;

import models.Customer;

public class CustomerDao extends AbstractDao<Customer> {

    public CustomerDao() {
        super(Customer.class, "customers");
    }

}
