package dao;

import models.Product;

import java.util.List;

public class ProductsDao extends AbstractDao<Product> {

    public ProductsDao() {
        super(Product.class, "products");
    }

}
