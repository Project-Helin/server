package controllers;

import com.google.inject.Inject;
import commons.SessionHelper;
import commons.order.MissionDispatchingService;
import dao.DroneDao;
import dao.ProjectsDao;
import models.Drone;
import models.Project;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.projectsDrones.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ProjectsDronesController extends Controller {

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private DroneDao droneDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private FormFactory formFactory;

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Security.Authenticated(SecurityAuthenticator.class)
    @Transactional
    public Result index(UUID projectId) {
        Project foundProject = getProject(projectId);

        if (foundProject == null) {
            return forbidden("Project not found!");
        }

        ArrayList<Drone> drones = new ArrayList<>(foundProject.getDrones());
        Collections.sort(drones, (a, b) -> a.getName().compareTo(b.getName()));

        // possible drones to add
        List<Drone> missingDrones = droneDao.findWithoutProjectByOrganisation(sessionHelper.getOrganisation(session()));
        missingDrones.removeAll(drones);

        return ok(index.render(projectId, drones, missingDrones));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    @Transactional
    public Result addDrone(UUID projectId) {
        Project foundProject = getProject(projectId);

        if (foundProject == null) {
            return forbidden("Project not found!");
        }

        Drone newDroneToAdd = getDroneFromRequest();
        if (newDroneToAdd == null) {
            return forbidden("Drone not found!");
        }

        newDroneToAdd.setProject(foundProject);
        droneDao.persist(newDroneToAdd);

        missionDispatchingService.tryToDispatchWaitingMissions(projectId);

        return redirect(routes.ProjectsDronesController.index(projectId));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    @Transactional
    public Result delete(UUID projectId, UUID droneId) {
        Project foundProject = getProject(projectId);

        if (foundProject == null) {
            return forbidden("Project not found!");
        }

        Drone droneToRemove = findDrone(droneId);
        if (droneToRemove == null) {
            return forbidden("Drone not found!");
        }

        droneToRemove.setProject(null);
        droneDao.persist(droneToRemove);

        return redirect(routes.ProjectsDronesController.index(projectId));
    }

    private Drone getDroneFromRequest() {
        DynamicForm dynamicForm = formFactory.form().bindFromRequest(request());
        String droneId = dynamicForm.get("droneId");
        return findDrone(UUID.fromString(droneId));
    }

    private Project getProject(UUID projectId) {
        return projectsDao.findByIdAndOrganisation(projectId, sessionHelper.getOrganisation(session()));
    }

    private Drone findDrone(UUID id) {
        return droneDao.findByIdAndOrganisation(id, sessionHelper.getOrganisation(session()));
    }
}
