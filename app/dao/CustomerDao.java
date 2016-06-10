package dao;

import models.Customer;
import models.Organisation;

import javax.persistence.TypedQuery;
import java.util.List;

public class CustomerDao extends AbstractDao<Customer> {

    public CustomerDao() {
        super(Customer.class, "customers");
    }

    public List<Customer> findCustomerByOrganisation(Organisation organisation) {
        TypedQuery<Customer> query = jpaApi.em().createQuery(
            "select o.customer from orders o " +
            " where o.project.organisation.id = :organisation",
            Customer.class
        );
        query.setParameter("organisation", organisation.getId());
        return query.getResultList();
    }
}
