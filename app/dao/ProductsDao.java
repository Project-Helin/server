package dao;

import models.Organisation;
import models.Product;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProductsDao extends AbstractDao<Product> {

    public ProductsDao() {
        super(Product.class, "products");
    }

    public List<Product> findByOrganisation(Organisation organisation) {
        Objects.requireNonNull(organisation);

        TypedQuery<Product> query = jpaApi.em().createQuery(
            "select p " +
            " from products p " +
            " where p.organisation = :organisation", Product.class);
        query.setParameter("organisation", organisation);
        return query.getResultList();
    }

    public Product findByIdAndOrganisation(UUID productId, Organisation organisation) {
        Objects.requireNonNull(productId);
        Objects.requireNonNull(organisation);

        TypedQuery<Product> query = jpaApi.em().createQuery(
            "select p from products  p " +
            " where p.organisation = :organisation " +
            " and p.id = :productId", Product.class);

        query.setParameter("organisation", organisation);
        query.setParameter("productId", productId);

        return query.getSingleResult();
    }
}
