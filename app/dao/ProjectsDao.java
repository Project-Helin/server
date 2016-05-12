package dao;

import commons.gis.GisHelper;
import models.Organisation;
import models.Project;
import org.geolatte.geom.*;
import org.slf4j.Logger;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;


import static org.slf4j.LoggerFactory.getLogger;

public class ProjectsDao extends AbstractDao<Project> {
    private static final Logger logger = getLogger(ProjectsDao.class);

    public ProjectsDao() {
        super(Project.class, "projects");
    }

    public List<Project> findByOrganisation(UUID organisationId) {
        return jpaApi.em()
                .createQuery(
                        "select e from projects e where e.organisation.id = :organisation ",
                        Project.class
                )
                .setParameter("organisation", organisationId)
                .getResultList();
    }

    public Project findByIdAndOrganisation(UUID projectId, Organisation organisation) {
        TypedQuery<Project> query = jpaApi
            .em()
            .createQuery(
                "select p from projects p " +
                " where p.organisation = :organisation " +
                " and p.id = :projectId",
                Project.class
            )
            .setParameter("projectId", projectId)
            .setParameter("organisation", organisation);

        return getSingleResultOrNull(query);
    }

}
