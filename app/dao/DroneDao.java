package dao;

import models.Drone;

import java.util.List;

public class DroneDao extends AbstractDao<Drone> {

    public DroneDao() {
        super(Drone.class);
    }

    @Override
    public List<Drone> findAll() {
        String sql = "select e from drones e ";
        return jpaApi.em()
                .createQuery(sql, Drone.class)
                .getResultList();
    }
}
