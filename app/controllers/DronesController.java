package controllers;

import com.google.inject.Inject;
import commons.ModelHelper;
import dao.DroneDao;
import models.Drone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.drones.edit;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

@Transactional
public class DronesController extends Controller {

    @Inject
    private DroneDao droneDao;

    @Inject
    private FormFactory formFactory;

    private static final Logger logger = LoggerFactory.getLogger(DronesController.class);

    public Result index() {
        List<Drone> all =
                droneDao.findAll();
        return ok(views.html.drones.index.render(all));
    }

    public Result edit(UUID id) {
        Drone found = droneDao.findById(id);

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

    public Result update(UUID id) throws InvocationTargetException, IllegalAccessException {
        Drone found = droneDao.findById(id);

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

    public Result delete(UUID id) {
        Drone found = droneDao.findById(id);

        if (found == null) {
            return forbidden("Drone not found!");
        }

        flash("success", "Deleted successfully");
        droneDao.delete(found);
        return redirect(routes.DronesController.index());
    }
}
