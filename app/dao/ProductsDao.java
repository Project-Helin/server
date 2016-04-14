package dao;

import controllers.ProjectDto;
import models.Organisation;
import models.Product;

import java.util.List;

public class ProductsDao extends AbstractDao<Product> {

    public ProductsDao() {
        super(Product.class);
    }

    @Override
    public List<Product> findAll() {
        String sql = "select e from products e ";
        return jpaApi.em()
                .createQuery(sql, Product.class)
                .getResultList();
    }
}
