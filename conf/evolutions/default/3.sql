# CREATE ZONE TABLE

# --- !Ups
CREATE TABLE zone
(
  id         UUID          NOT NULL PRIMARY KEY,

  --- 4326 is EPSG-Code for WGS84 projection
  polygon    GEOMETRY(POLYGON, 4326),
  height     INTEGER       NOT NULL,
  TYPE       VARCHAR(200)  NOT NULL,
  name       VARCHAR(2000) NOT NULL,
  project_id UUID          NOT NULL REFERENCES project (id)
);


# --- !Downs

DROP TABLE zone;