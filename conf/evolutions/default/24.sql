# Add height to waypoint

# --- !Ups
ALTER TABLE drones add column waypoint double precision;

# --- !Downs
ALTER TABLE drones drop column waypoint;

