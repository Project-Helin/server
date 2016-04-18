# CREATE ZONE TABLE

# --- !Ups
CREATE TABLE project
(
  id              UUID         NOT NULL PRIMARY KEY,
  CREATED_AT      TIMESTAMP    NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT      TIMESTAMP    NOT NULL DEFAULT CURRENT_DATE,

  name            VARCHAR(200) NOT NULL,
  organisation_id UUID         NOT NULL REFERENCES organisation (id)
);

# --- !Downs

DROP TABLE project;
