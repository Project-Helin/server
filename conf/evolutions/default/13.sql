# CREATE WAYPOINTS TABLE

# --- !Ups

CREATE TABLE way_points (
  id                UUID                             NOT NULL PRIMARY KEY,
  order_number      SMALLINT                         NOT NULL,
  position          GEOGRAPHY(POINT, 4326)           NOT NULL,
  action            Varchar(20)                      NOT NULL,


  CREATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,

  route_id UUID REFERENCES routes (id),

  CONSTRAINT order_route_unique_constraint UNIQUE (route_id, order_number)
);

# --- !Downs

DROP TABLE way_points;