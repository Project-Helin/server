# CREATE ORGANISATION_USER TABLE

# --- !Ups

CREATE TABLE organisations_users(
  id                    SERIAL                NOT NULL PRIMARY KEY,
  organisation_id       UUID REFERENCES organisation (id)   NOT NULL,
  user_id               UUID REFERENCES users (id) NOT NULL,
  created_at            DATE                NOT NULL default CURRENT_DATE,
  updatet_at            DATE                NOT NULL default CURRENT_DATE
);

# --- !Downs

DROP TABLE organisations_users;
