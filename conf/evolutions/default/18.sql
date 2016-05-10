# ADD State AND IsActive TO drones

# --- !Ups

ALTER TABLE drones ADD
  is_active BOOLEAN DEFAULT TRUE;

# --- !Downs

ALTER TABLE drones
  DROP COLUMN is_active;
