package dao;

import models.Project;

import java.util.List;
import java.util.UUID;

public class ProjectsDao extends AbstractDao<Project> {

    public ProjectsDao() {
        super(Project.class);
    }

    public List<Project> findByOrganisation(UUID organisationId) {
        return jpaApi.em()
                .createQuery(
                        "select e from Project e where e.organisation.id = :organisation ",
                        Project.class
                )
                .setParameter("organisation", organisationId)
                .getResultList();
    }
}
