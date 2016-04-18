# CREATE USER TABLE

# --- !Ups

CREATE TABLE products
(
  id              UUID         NOT NULL PRIMARY KEY,
  CREATED_AT      TIMESTAMP    NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT      TIMESTAMP    NOT NULL DEFAULT CURRENT_DATE,

  name            VARCHAR(255) NOT NULL,
  price           DECIMAL,
  weight_gramm    INTEGER      NOT NULL,
  organisation_id UUID REFERENCES organisations (id)
);

# --- !Downs

DROP TABLE products;
