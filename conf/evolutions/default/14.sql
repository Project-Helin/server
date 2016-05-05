# Add current mission to drones

# --- !Ups

ALTER TABLE drones
ADD current_mission_id     UUID        REFERENCES missions (id);

# --- !Downs

ALTER TABLE drones
DROP COLUMN current_mission_id;
