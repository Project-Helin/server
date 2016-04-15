package controllers;

import com.google.inject.Inject;
import dao.OrganisationsDao;
import dao.ProjectsDao;
import models.Organisation;
import models.Project;
import models.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.projects.edit;
import views.html.projects.index;

import java.util.*;
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

    public Result add() {
        UUID newProjectId = UUID.randomUUID();
        return ok(edit.render(newProjectId));
    }

    public Result create() {
        UUID id = null; // this is null by intention
        // null means in this context, that no a new project should be created
        return ok(edit.render(id));
    }

    public Result edit(UUID id) {
        return ok(edit.render(id));
    }

    public Result delete(UUID projectId) {
        Project found = projectsDao.findById(projectId);

        if (found == null) {
            return forbidden("Projects not found!");
        }

        projectsDao.delete(found);
        return redirect(routes.ProjectsController.index());
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
}
