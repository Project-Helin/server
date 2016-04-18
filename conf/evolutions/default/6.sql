# CREATE DRONE TABLE

# --- !Ups

CREATE TABLE drones (
  id                  UUID                                NOT NULL PRIMARY KEY,
  CREATED_AT          TIMESTAMP                           NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT          TIMESTAMP                           NOT NULL DEFAULT CURRENT_DATE,

  name                VARCHAR(255)                        NOT NULL,
  last_known_position GEOGRAPHY(POINT, 4326),
  payload             INTEGER,
  token               UUID                                NOT NULL,
  organisation_id     UUID REFERENCES organisation (id)   NOT NULL,
  project_id          UUID REFERENCES project (id)
);

# --- !Downs

DROP TABLE drones;
