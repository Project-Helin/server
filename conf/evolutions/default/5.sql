# CREATE USER TABLE

# --- !Ups

CREATE TABLE products
(
  id              UUID         NOT NULL PRIMARY KEY,

  name            VARCHAR(255) NOT NULL,
  price           DECIMAL,
  weight_gramm    INTEGER      NOT NULL,
  organisation_id UUID REFERENCES organisation (id)
);

# --- !Downs

DROP TABLE products;
