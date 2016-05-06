# CREATE ORDERS TABLE

# --- !Ups

CREATE TABLE orders (
  id                UUID      NOT NULL PRIMARY KEY,

  CREATED_AT        TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT        TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,

  project_id        UUID REFERENCES projects (id),
  customer_id       UUID REFERENCES customers (id),
  delivery_position GEOGRAPHY(POINT, 4326)
);

# --- !Downs

DROP TABLE orders;
