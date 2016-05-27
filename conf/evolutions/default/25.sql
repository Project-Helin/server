# Add height to waypoint

# --- !Ups
ALTER TABLE way_points add column height double precision;
ALTER TABLE drones drop column waypoint;

# --- !Downs
ALTER TABLE way_points drop column height;
ALTER TABLE drones add column waypoint double precision;

