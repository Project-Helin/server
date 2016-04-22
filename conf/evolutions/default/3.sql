# CREATE ZONE TABLE

# --- !Ups
CREATE TABLE zones
(
  id         UUID          NOT NULL PRIMARY KEY,
  CREATED_AT TIMESTAMP     NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP     NOT NULL DEFAULT CURRENT_DATE,

  --- 4326 is EPSG-Code for WGS84 projection
  polygon    GEOGRAPHY(POLYGON, 4326),
  height     INTEGER       NOT NULL,
  TYPE       VARCHAR(200)  NOT NULL,
  name       VARCHAR(2000) NOT NULL,
  project_id UUID          NOT NULL REFERENCES projects (id)
);


# --- !Downs

DROP TABLE zones;