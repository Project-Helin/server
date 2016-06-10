package controllers;

import com.google.inject.Inject;
import service.SessionHelper;
import dao.OrderDao;
import dao.ProjectsDao;
import models.Order;
import models.Project;
import org.apache.commons.lang3.StringUtils;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.orders.index;
import views.html.orders.show;

import java.util.List;
import java.util.UUID;

@Transactional
public class OrdersController extends Controller {

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

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result show(UUID orderId) {

        Order order = orderDao.findById(orderId);

        if (order.getProject().getOrganisation() != sessionHelper.getOrganisation(session())) {
            return forbidden();
        } else {
            return ok(show.render(orderId));
        }
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result delete(UUID ordersId) {
        Order found = orderDao.findById(ordersId);
        if (found == null) {
            return forbidden();
        }

        found.getOrderProducts().clear();
        found.getMissions().clear();
        orderDao.persist(found);
        orderDao.delete(found);

        return redirect(routes.OrdersController.index());
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
