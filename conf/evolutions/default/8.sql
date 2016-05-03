# CREATE PROJECTS_PRODUCTS TABLE

# --- !Ups

CREATE TABLE projects_products (
  id         SERIAL                                  NOT NULL PRIMARY KEY,
  CREATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,

  project_id UUID REFERENCES projects (id)           NOT NULL,
  product_id UUID REFERENCES products (id)           NOT NULL
);

# --- !Downs

DROP TABLE projects_products;
