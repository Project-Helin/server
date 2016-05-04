# CREATE PROJECTS_DRONES TABLE

# --- !Ups

CREATE TABLE projects_drones (
  id         SERIAL                                  NOT NULL PRIMARY KEY,
  CREATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,

  project_id UUID REFERENCES projects (id)           NOT NULL,
  drone_id   UUID REFERENCES drones (id)             NOT NULL
);

# --- !Downs

DROP TABLE projects_drones;
