package controllers;

import com.google.inject.Inject;
import dao.OrganisationsDao;
import dao.ProjectsDao;
import models.Project;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.projects.edit;
import views.html.projects.index;

import java.util.List;
import java.util.UUID;

@Transactional
public class ProjectsController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private OrganisationsDao organisationsDao;

    public Result index() {
        UUID organisationId = getOrganisationId();

        logger.info("Organisation id {}", organisationId);
        List<Project> all = projectsDao.findByOrganisation(organisationId);

        return ok(index.render(all));
    }

    private UUID getOrganisationId() {
        /**
         * TODO Kiru: this should from from the current user
         */
        return UUID.fromString("556963dd-94a3-46b0-9576-a27cb39fe62b");
    }

    public Result add() {
        UUID newProjectId = UUID.randomUUID();
        return ok(edit.render(newProjectId));
    }

    public Result create() {
        UUID id = UUID.randomUUID();
        /**
         * This is an angular page - so not that much to do here.
         */
        return ok(edit.render(id));
    }

    public Result edit(UUID id) {
        /**
         * This is an angular page - so not that much to do here.
         */
        return ok(edit.render(id));
    }


    /**
     * @return Project as json
     */
    public Result show(UUID projectID) {
        Project found = projectsDao.findById(projectID);
        if (found == null) {
            return forbidden("Organisation not found!");
        }

        ProjectDto projectDto = new ProjectDto(
                found.getId(),
                found.getName(),
                found.getHeadquarterPosition()
        );

        return ok(Json.toJson(projectDto));
    }

    public Result update(UUID projectId) {
        ProjectDto projectDto = Json.fromJson(request().body().asJson(), ProjectDto.class);
        System.out.println(ToStringBuilder.reflectionToString(projectDto));

        Project project = new Project();
        project.setId(projectDto.getId());
        project.setName(projectDto.getName());
        project.setHeadquarterPosition(projectDto.getHeadquarterPosition());
        project.setOrganisation(organisationsDao.findById(getOrganisationId()));

        projectsDao.persist(project);
        return ok();
    }

    public Result delete(UUID projectID) {
        Project found = projectsDao.findById(projectID);

        if (found == null) {
            return forbidden("Organisation not found!");

        }

        projectsDao.delete(found);
        return index();
    }

}
