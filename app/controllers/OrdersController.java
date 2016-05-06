package controllers;

import com.google.inject.Inject;
import commons.SessionHelper;
import dao.OrderDao;
import dao.ProjectsDao;
import models.Order;
import models.Project;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.orders.index;

import java.util.List;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Transactional
public class OrdersController extends Controller {

    private static final Logger logger = getLogger(OrdersController.class);

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private OrderDao orderDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        String selectedProject = getSelectedProject();

        boolean noProjectSelected = StringUtils.isBlank(selectedProject);
        if (noProjectSelected) {
            return showAll();
        }

        UUID projectId = UUID.fromString(selectedProject);

        List<Order> orders =
            orderDao.findByProjectId(projectId, sessionHelper.getOrganisation(session()));
        List<Project> projects = findAllProjects();

        return ok(index.render(projects, orders, selectedProject));
    }

    private Result showAll() {
        List<Order> orders =
            orderDao.findByOrganisation(sessionHelper.getOrganisation(session()));

        List<Project> projects = findAllProjects();

        return ok(index.render(projects, orders, ""));
    }

    private String getSelectedProject() {
        DynamicForm form = formFactory.form().bindFromRequest(request());
        return form.get("selectedProject");
    }

    private List<Project> findAllProjects() {
        return projectsDao.findByOrganisation(sessionHelper.getOrganisation(session()).getId());
    }

}
