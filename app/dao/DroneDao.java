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

    public Drone findMatchingPayloadAndHighestBatteryRemain(UUID projectId, int payload) {
        TypedQuery<Drone> droneTypedQuery = jpaApi
                .em()
                .createQuery(
                        "select d from drones d " +
                                " where d.project.id = :project_id " +
                                " and d.payload >= :payload " +
                                " and d.currentMission = null " +
                                " and abs(1 - d.payload) = (select min( abs(1 - t.payload)) from drones t) "
                        ,
                        Drone.class
                )
                .setParameter("project_id", projectId)
                .setParameter("payload", payload);

        return getSingleResultOrNull(droneTypedQuery);
    }
}
