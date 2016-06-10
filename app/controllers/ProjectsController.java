package controllers;

import com.google.inject.Inject;
import service.SessionHelper;
import dao.ProjectsDao;
import models.Project;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.projects.edit;
import views.html.projects.index;

import java.util.List;
import java.util.UUID;

@Transactional
public class ProjectsController extends Controller {

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private SessionHelper sessionHelper;

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        UUID organisationId = sessionHelper.getOrganisation(session()).getId();
        List<Project> all = projectsDao.findByOrganisation(organisationId);

        return ok(index.render(all));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result add() {
        UUID id = null;
        // this is null by intention
        // null means in this context, that no a new project should be created
        return ok(edit.render(id));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result edit(UUID id) {
        return ok(edit.render(id));
    }
    
    @Security.Authenticated(SecurityAuthenticator.class)
    public Result delete(UUID projectId) {
        Project found = projectsDao.findByIdAndOrganisation(projectId, sessionHelper.getOrganisation(session()));

        if (found == null) {
            return forbidden("Projects not found!");
        }

        projectsDao.delete(found);
        return redirect(routes.ProjectsController.index());
    }

}
