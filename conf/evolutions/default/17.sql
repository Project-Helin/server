# CREATE ORDERS_PRODUCTS TABLE

# --- !Ups

CREATE TABLE orders_products (
  id          UUID             NOT NULL PRIMARY KEY,

  CREATED_AT  TIMESTAMP        NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT  TIMESTAMP        NOT NULL DEFAULT CURRENT_DATE,

  order_id    UUID REFERENCES orders (id),
  product_id  UUID REFERENCES products (id),
  amount      INTEGER          NOT NULL,
  total_price DOUBLE PRECISION NOT NULL
);

# --- !Downs

DROP TABLE orders_products;
