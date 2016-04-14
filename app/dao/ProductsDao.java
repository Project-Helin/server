package dao;

import controllers.ProjectDto;
import models.Organisation;
import models.Product;

import java.util.List;

public class ProductsDao extends AbstractDao<Product> {

    public ProductsDao() {
        super(Product.class);
    }

}
