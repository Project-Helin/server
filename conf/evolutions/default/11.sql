# CREATE DRONE_INFOS TABLE

# --- !Ups

CREATE TABLE drone_infos (
  id         UUID                                    NOT NULL PRIMARY KEY,
  remaining_battery_percent     DOUBLE PRECISION,
  battery_discharge             DOUBLE PRECISION,
  battery_voltage               DOUBLE PRECISION,
  altitude                      DOUBLE PRECISION,
  target_altitude               DOUBLE PRECISION,
  vertical_speed                DOUBLE PRECISION,
  ground_speed                  DOUBLE PRECISION,
  is_connected_to_drone         BOOLEAN,
  satellites_count              SMALLINT,
  client_time                   TIMESTAMP           NOT NULL,
  drone_position                GEOGRAPHY(POINT, 4326),
  phone_position                GEOGRAPHY(POINT, 4326),



  CREATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,
  UPDATED_AT TIMESTAMP                               NOT NULL DEFAULT CURRENT_DATE,

  mission_id UUID REFERENCES missions (id),
  drone_id   UUID REFERENCES drones (id)             NOT NULL
);

# --- !Downs

DROP TABLE drone_infos;