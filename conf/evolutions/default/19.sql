# ADD additional fields to Mission

# --- !Ups

ALTER TABLE missions ADD
  order_id UUID NOT NULL REFERENCES orders(id);

ALTER TABLE missions ADD
  order_product_id UUID NOT NULL REFERENCES orders_products(id);

# --- !Downs

ALTER TABLE missions
  DROP COLUMN order_id;

ALTER TABLE missions
  DROP COLUMN order_product_id;
