package controllers;

import ch.helin.messages.dto.message.DroneDtoMessage;
import com.google.inject.Inject;
import commons.ModelHelper;
import commons.SessionHelper;
import commons.drone.DroneCommunicationManager;
import commons.order.MissionDispatchingService;
import dao.DroneDao;
import mappers.DroneMapper;
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

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Inject
    private DroneMapper droneMapper;

    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    private static final Logger logger = LoggerFactory.getLogger(DronesController.class);

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        List<Drone> all = droneDao.findByOrganisation(getOrganisation());

        String organisationToken = getOrganisation().getToken();
        return ok(index.render(all, organisationToken));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
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

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result update(UUID id) {
        Drone foundDrone = getDroneById(id);

        if (foundDrone == null) {
            return forbidden("Drone not foundDrone!");
        }

        Form<Drone> form = formFactory
                .form(Drone.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(edit.render(form));
        } else {

            ModelHelper.updateAttributes(foundDrone, form.get());
            if (form.get().getIsActive() == null) {
                foundDrone.setIsActive(false);
            }
            droneDao.persist(foundDrone);
            flash("success", "Saved successfully");

            DroneDtoMessage droneDtoMessage = new DroneDtoMessage();
            droneDtoMessage.setDroneDto(droneMapper.getDroneDto(foundDrone));

            droneCommunicationManager.sendMessageToDrone(foundDrone.getId(), droneDtoMessage);

            if(foundDrone.getIsActive() == false) {
                missionDispatchingService.withdrawDroneFromMission(foundDrone);
            }

            if (foundDrone.getProject() != null && foundDrone.getIsActive() == true) {
                missionDispatchingService.tryToDispatchWaitingMissions(foundDrone.getProject().getId());
            }

            return redirect(routes.DronesController.index());
        }
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result delete(UUID droneId) {
        Drone found = getDroneById(droneId);

        if (found == null) {
            return forbidden("Drone not found!");
        }

        flash("success", "Deleted successfully");
        found.getDroneInfos().clear();;
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
