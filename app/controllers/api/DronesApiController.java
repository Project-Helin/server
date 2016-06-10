package controllers.api;

import ch.helin.messages.dto.message.DroneDto;
import ch.helin.messages.dto.message.RabbitMqInformation;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import service.SettingsHelper;
import service.drone.DroneCommunicationManager;
import service.order.MissionDispatchingService;
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

    @Inject
    private SettingsHelper settingsHelper;

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
            Organisation organisation = getOrganisation(organisationToken);
            if (organisation == null) {
                return badRequest("Wrong_Organisation_Token");
            }

            Drone drone = createDrone(name, payload, organisation);
            DroneDto droneDto = droneMapper.getDroneDto(drone);

            RabbitMqInformation rabbitMq = new RabbitMqInformation();
            rabbitMq.setUsername(settingsHelper.getRabbitMQUserName());
            rabbitMq.setPassword(settingsHelper.getRabbitMQPassword());
            droneDto.setRabbitMqInformation(rabbitMq);

            return ok(Json.toJson(droneDto));
        }
    }

    private Drone createDrone(String name, int payload, Organisation organisation) {
        Drone drone = new Drone();
        drone.setName(name);
        drone.setPayload(payload);
        drone.setOrganisation(organisation);
        drone.setId(UUID.randomUUID());
        drone.setToken(UUID.randomUUID());
        drone.setIsActive(true);

        droneDao.persist(drone);
        droneCommunicationManager.addDrone(drone);

        if (drone.getProject() != null) {
            missionDispatchingService.tryToDispatchWaitingMissions(drone.getProject().getId());
        }
        
        return drone;
    }

    private Organisation getOrganisation(String organisationToken) {
        return organisationsDao.findByOrganisationToken(organisationToken);
    }
}
