# CREATE Organisation TABLE

# --- !Ups

CREATE TABLE Organisation (
  ID   UUID         NOT NULL PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL,
  TOKEN VARCHAR(255)
);

# --- !Downs

DROP TABLE Organisation;
