# ADD State TO orders

# --- !Ups

ALTER TABLE orders ADD
  state VARCHAR(32) DEFAULT 'NEW';

# --- !Downs

ALTER TABLE orders
  DROP COLUMN state;
