package dao;

import models.Order;
import models.Organisation;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderDao extends AbstractDao<Order> {

    public OrderDao() {
        super(Order.class, "orders");
    }

    public List<Order> findByOrganisation(Organisation organisation) {
        Objects.requireNonNull(organisation);

        TypedQuery<Order> query = jpaApi.em().createQuery(
                "select p " +
                        " from orders p " +
                        " where p.project.organisation = :organisation", Order.class);
        query.setParameter("organisation", organisation);
        return query.getResultList();
    }

    public List<Order> findByProjectId(UUID projectId, Organisation organisation) {
        Objects.requireNonNull(organisation);

        TypedQuery<Order> query = jpaApi.em().createQuery(
                "select p " +
                        " from orders p " +
                        " where p.project.organisation = :organisation " +
                        " and p.project.id = :projectId ", Order.class);

        query.setParameter("organisation", organisation);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }

    public List<Order> findByCustomer(UUID customerId) {
        TypedQuery<Order> query = jpaApi.em().createQuery(
            "select p " +
                " from orders p " +
                " where p.customer.id = :customerId ", Order.class);

        query.setParameter("customerId", customerId);
        return query.getResultList();
    }
}
