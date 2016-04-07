package controllers;

import com.google.inject.Inject;
import dao.ProjectsDao;
import models.Organisation;
import models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.projects.add;
import views.html.projects.index;
import views.html.projects.edit;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
public class ProjectsController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private FormFactory formFactory;

    public Result index(UUID organisationId) {
        logger.info("Organisation id {}", organisationId);
        List<Project> all = projectsDao
                .findAll()
                .stream()
                .filter(o -> o.getOrganisation().getId().equals(organisationId))
                .collect(Collectors.toList());

        return ok(index.render(organisationId, all));
    }

    public Result add(UUID organisationId) {
        Form<Project> form = formFactory
                .form(Project.class)
                .fill(new Project());

        return ok(add.render(organisationId, form));
    }

    public Result create(UUID organisationId) {

        Form<Organisation> form = formFactory
                .form(Organisation.class)
                .bindFromRequest(request());

        Organisation organisation = form.get();
        organisation.setId(UUID.randomUUID());
//         projectsDao.persist(organisation);

        return index(organisationId);
    }

    public Result edit(UUID organisationId, UUID id) {
        Project project = projectsDao.findById(id);

        if (project == null) {
            return forbidden("Project not found!");
        }

        return ok(edit.render(project));
    }

    public Result update(UUID organisationId, UUID id) {
        Organisation found = null; // organisationsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }
//        organisationsDao.persist(found);

        return index(organisationId);
    }

    public Result delete(UUID organisationId, UUID id) {
        Project found = projectsDao.findById(id);
        if (found == null) {
            return forbidden("Organisation not found!");

        }

        projectsDao.delete(found);
        return index(organisationId);
    }
}
