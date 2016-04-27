package controllers;

import com.google.inject.Inject;
import commons.ModelHelper;
import commons.SessionHelper;
import dao.DroneDao;
import models.Drone;
import models.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.drones.edit;
import views.html.drones.index;

import java.util.List;
import java.util.UUID;

@Transactional
public class DronesController extends Controller {

    @Inject
    private DroneDao droneDao;

    @Inject
    private FormFactory formFactory;

    @Inject
    private SessionHelper sessionHelper;

    private static final Logger logger = LoggerFactory.getLogger(DronesController.class);

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        List<Drone> all = droneDao.findByOrganisation(getOrganisation());
        String organisationToken = getOrganisation().getToken();
        return ok(index.render(all, organisationToken));
    }

    public Result edit(UUID id) {
        Drone found = getDroneById(id);

        if (found == null) {
            return forbidden("Drone not found!");
        }

        Form<Drone> form = formFactory
                .form(Drone.class)
                .fill(found);

        if (form.hasErrors()) {
            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(edit.render(form));
        } else {
            return ok(edit.render(form));
        }
    }

    public Result update(UUID id) {
        Drone found = getDroneById(id);

        if (found == null) {
            return forbidden("Drone not found!");
        }

        Form<Drone> form = formFactory
                .form(Drone.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(edit.render(form));
        } else {

            ModelHelper.updateAttributes(found, form.get());
            droneDao.persist(found);
            flash("success", "Saved successfully");

            return redirect(routes.DronesController.index());
        }
    }

    public Result delete(UUID droneId) {
        Drone found = getDroneById(droneId);

        if (found == null) {
            return forbidden("Drone not found!");
        }

        flash("success", "Deleted successfully");
        droneDao.delete(found);
        return redirect(routes.DronesController.index());
    }


    private Drone getDroneById(UUID id) {
        return droneDao.findByIdAndOrganisation(id, getOrganisation());
    }


    private Organisation getOrganisation() {
        return sessionHelper.getOrganisation(session());
    }
}
