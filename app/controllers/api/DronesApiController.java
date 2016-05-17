package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import commons.drone.DroneCommunicationManager;
import commons.order.MissionDispatchingService;
import dao.DroneDao;
import dao.OrganisationsDao;
import mappers.DroneMapper;
import models.Drone;
import models.Organisation;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

public class DronesApiController extends Controller {

    @Inject
    private DroneDao droneDao;

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private MissionDispatchingService missionDispatchingService;

    @Inject
    private DroneMapper droneMapper;

    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public Result create () {
        JsonNode json = request().body().asJson();
        String name = json.findPath("name").textValue();
        int payload = Integer.valueOf(json.findPath("payload").textValue());
        String organisationToken = json.findPath("organisationToken").textValue();

        if(name == null) {
            return badRequest("Missing_Parameter_Name");
        } else if (organisationToken == null) {
            return badRequest("Missing_Parameter_OrganisationToken");
        } else {
            Drone drone = new Drone();
            drone.setName(name);
            drone.setPayload(payload);
            Organisation organisation = getOrganisation(organisationToken);
            if (organisation == null) {
                return badRequest("Wrong_Organisation_Token");
            }
            drone.setOrganisation(organisation);

            drone.setId(UUID.randomUUID());
            drone.setToken(UUID.randomUUID());

            droneDao.persist(drone);

            droneCommunicationManager.addDrone(drone);

            drone.getProjects().stream().forEach((p) -> missionDispatchingService.tryToDispatchWaitingMissions(p.getId()));

            return ok(Json.toJson(droneMapper.getDroneDto(drone)));
        }
    }

    private Organisation getOrganisation(String organisationToken) {
        return organisationsDao.findByOrganisationToken(organisationToken);
    }
}
