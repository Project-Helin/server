# CREATE Organisation TABLE

# --- !Ups

CREATE TABLE Organisation (
  ID   UUID         NOT NULL PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL
);

-- For now remove this later
INSERT INTO organisation VALUES (uuid_generate_v4(), 'HSR');

# --- !Downs

DROP TABLE Organisation;
