package dao;

import models.Organisation;
import models.Project;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

public class ProjectsDao extends AbstractDao<Project> {

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
