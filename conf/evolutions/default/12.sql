# CREATE ROUTES TABLE

# --- !Ups

CREATE TABLE routes (
  id         UUID                                    NOT NULL PRIMARY KEY,

  CREATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,

  mission_id UUID REFERENCES missions (id)
);

# --- !Downs

DROP TABLE routes;