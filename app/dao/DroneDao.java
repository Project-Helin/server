package dao;

import models.Drone;
import models.Organisation;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

public class DroneDao extends AbstractDao<Drone> {

    public DroneDao() {
        super(Drone.class, "drones");
    }

    public List<Drone> findByOrganisation(Organisation organisation) {
        return jpaApi
            .em()
            .createQuery("select d from drones d where d.organisation = :organisation", Drone.class)
            .setParameter("organisation", organisation)
            .getResultList();
    }

    public Drone findByIdAndOrganisation(UUID droneId, Organisation organisation) {
        TypedQuery<Drone> droneTypedQuery = jpaApi
            .em()
            .createQuery(
                "select d from drones d " +
                    " where d.organisation = :organisation and d.id = :droneId",
                Drone.class
            )
            .setParameter("organisation", organisation)
            .setParameter("droneId", droneId);

        return getSingleResultOrNull(droneTypedQuery);
    }
}
