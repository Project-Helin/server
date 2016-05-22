# RENAME delivery_position to customer_position

# --- !Ups
ALTER TABLE orders RENAME delivery_position to customer_position;

# --- !Downs
ALTER TABLE orders RENAME customer_position to delivery_position;

