package dao;

import models.Drone;

import java.util.List;

public class DroneDao extends AbstractDao<Drone> {

    public DroneDao() {
        super(Drone.class, "drones");
    }

}
