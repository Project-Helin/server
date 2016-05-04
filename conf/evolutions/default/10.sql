# CREATE MISSIONS TABLE

# --- !Ups

CREATE TABLE missions (
  id         UUID                                    NOT NULL PRIMARY KEY,
  state                         VARCHAR(32),

  CREATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE
);

# --- !Downs

DROP TABLE missions;