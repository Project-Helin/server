# ADD max item per drone to product

# --- !Ups

ALTER TABLE products ADD
  max_item_per_drone INTEGER DEFAULT 1;

# --- !Downs

ALTER TABLE products
  DROP COLUMN max_item_per_drone;
