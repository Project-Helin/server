# CREATE ORGANISATION_USER TABLE

# --- !Ups

CREATE TABLE organisations_users (
  id              SERIAL                              NOT NULL PRIMARY KEY,
  CREATED_AT      TIMESTAMP                           NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT      TIMESTAMP                           NOT NULL DEFAULT CURRENT_DATE,

  organisation_id UUID REFERENCES organisations (id)   NOT NULL,
  user_id         UUID REFERENCES users (id)          NOT NULL
);

# --- !Downs

DROP TABLE organisations_users;
