package dao;

import models.OrderProduct;

public class OrderProductDao extends AbstractDao<OrderProduct> {

    public OrderProductDao() {
        super(OrderProduct.class, "orders_products");
    }
}

