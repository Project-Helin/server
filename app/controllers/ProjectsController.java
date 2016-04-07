package controllers;

import com.google.inject.Inject;
import commons.GisHelper;
import dao.OrganisationsDao;
import dao.ProjectsDao;
import models.Organisation;
import models.Project;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
public class ProjectsController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private OrganisationsDao organisationsDao;

    public Result index() {
        UUID organisationId = getOrganisation().getId();

        logger.info("Organisation id {}", organisationId);
        List<Project> all = projectsDao.findByOrganisation(organisationId);

        return ok(index.render(all));
    }

    private Organisation getOrganisation() {

        /**
         * For now -> HSR is always there
         */
        return organisationsDao
                .findAll()
                .stream()
                .filter(new Predicate<Organisation>() {
                    @Override
                    public boolean test(Organisation organisation) {
                        return organisation.getName().equals("HSR");
                    }
                })
                .collect(Collectors.toList()).get(0);
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
        Project project = projectsDao.findById(projectId);

        boolean isNewProject = project == null;
        if (isNewProject) {
            // create new project
            project = new Project();
            project.setId(UUID.randomUUID());
            project.setOrganisation(getOrganisation());
        }

        ProjectDto fromRequest =
                Json.fromJson(request().body().asJson(), ProjectDto.class);
        // set all fields
        project.setName(fromRequest.getName());
        // TODO remove hard-coding
        project.setHeadquarterPosition(GisHelper.createPoint(10, 10));
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
