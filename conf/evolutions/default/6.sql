# CREATE DRONE TABLE

# --- !Ups

CREATE TABLE drones(
  id                    UUID                NOT NULL PRIMARY KEY,

  name                  VARCHAR(255)        NOT NULL,
  last_known_position   GEOGRAPHY(POINT, 4326),
  payload               INTEGER,
  token                 UUID                NOT NULL,
  organisation_id       UUID REFERENCES organisation (id)   NOT NULL,
  project_id            UUID REFERENCES project (id),
  created_at            DATE                NOT NULL default CURRENT_DATE,
  updatet_at            DATE                NOT NULL default CURRENT_DATE
);

# --- !Downs

DROP TABLE drones;
