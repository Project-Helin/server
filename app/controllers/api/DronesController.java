package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import dao.DroneDao;
import dao.OrganisationsDao;
import models.Drone;
import models.Organisation;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;

import java.util.UUID;
import java.util.stream.Collectors;

public class DronesController extends Controller {
    @Inject
    DroneDao droneDao;

    @Inject
    OrganisationsDao organisationsDao;

    @BodyParser.Of(BodyParser.Json.class)
    public play.mvc.Result create () {
        JsonNode json = request().body().asJson();
        String name = json.findPath("name").textValue();
        int payload = Integer.valueOf(json.findPath("payload").textValue());
        String organisationToken = json.findPath("organisationToken").textValue();

        if(name == null) {
            return badRequest("Missing parameter name");
        } else if (organisationToken == null) {
            return badRequest("Missing parameter organisationToken");
        } else {
            Drone drone = new Drone();
            drone.setName(name);
            drone.setPayload(payload);
            drone.setOrganisation(getOrganisation(organisationToken));

            drone.setId(UUID.randomUUID());
            drone.setToken(UUID.randomUUID());

            droneDao.persist(drone);

            return ok(Json.toJson(new Object()));
        }
    }

    private Organisation getOrganisation(String organisationToken) {

        //TODO load organisation from token, now its always HSR
        return organisationsDao
                .findAll()
                .stream()
                .filter( e -> e.getName().equals("HSR"))
                .collect(Collectors.toList()).get(0);
    }
}
