package controllers;

import com.google.inject.Inject;
import commons.SessionHelper;
import dao.ProjectsDao;
import models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.Transactional;
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
    private SessionHelper sessionHelper;

    public Result index() {
        UUID organisationId = sessionHelper.getOrganisation(session()).getId();

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
        Project found = projectsDao.findByIdAndOrganisation(projectId, sessionHelper.getOrganisation(session()));

        if (found == null) {
            return forbidden("Projects not found!");
        }

        projectsDao.delete(found);
        return redirect(routes.ProjectsController.index());
    }

}
