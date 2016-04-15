package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import commons.DroneConnection;
import dao.DroneDao;
import dao.OrganisationsDao;
import models.Drone;
import models.Organisation;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

public class DronesController extends Controller {

    @Inject
    private DroneDao droneDao;

    @Inject
    private OrganisationsDao organisationsDao;

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
            drone.setOrganisation(getOrganisation(organisationToken));

            drone.setId(UUID.randomUUID());
            drone.setToken(UUID.randomUUID());

            droneDao.persist(drone);

            createDroneConnection(drone);

            return ok(Json.toJson(drone));
        }
    }

    private void createDroneConnection(Drone drone) {
        new DroneConnection(drone);
    }

    private Organisation getOrganisation(String organisationToken) {
        return organisationsDao.findByOrganisationToken(organisationToken);
    }
}
