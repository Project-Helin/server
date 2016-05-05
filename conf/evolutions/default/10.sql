# CREATE MISSIONS TABLE

# --- !Ups

CREATE TABLE missions (
  id            UUID                                 NOT NULL PRIMARY KEY,
  state         VARCHAR(32),
  drone_id      UUID                                 REFERENCES drones(id) NOT NULL,

  CREATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE
);

# --- !Downs

DROP TABLE missions;