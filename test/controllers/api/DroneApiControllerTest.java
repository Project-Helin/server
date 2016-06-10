package controllers.api;

import ch.helin.messages.dto.message.DroneDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import models.Drone;
import models.Organisation;
import models.User;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class DroneApiControllerTest extends AbstractWebServiceIntegrationTest {

    @Inject
    private WSClient ws;

    @Inject
    private ApiHelper apiHelper;

    private static final String BASE_URL = "http://localhost:19001";

    @Test
    public void createDrone() {
        User user = jpaApi.withTransaction(em -> testHelper.createUserWithOrganisation("bla"));

        Organisation organisation = user.getOrganisations().stream().findFirst().get();

        Drone newDrone = new Drone();
        newDrone.setName("new drone");
        newDrone.setPayload(500);

        JsonNode jsonDrone = Json.newObject()
                .put("name", newDrone.getName())
                .put("payload", String.valueOf(newDrone.getPayload()))
                .put("organisationToken", organisation.getToken());

        JsonNode wrapper = Json.newObject()
                .set("drone", jsonDrone);

        DroneDto returnedDrone = apiHelper.doPost(routes.DronesApiController.create(), wrapper, DroneDto.class);

        assertNotNull(returnedDrone.getId());
        assertThat(returnedDrone.getName()).isEqualTo(newDrone.getName());
        assertThat(returnedDrone.getPayload()).isEqualTo(newDrone.getPayload());
        assertNotNull(returnedDrone.getOrganisationToken());
        assertNotNull(returnedDrone.getToken());

        assertNotNull(returnedDrone.getRabbitMqInformation());
        assertThat(returnedDrone.getRabbitMqInformation().getUsername()).isNotEmpty();
        assertThat(returnedDrone.getRabbitMqInformation().getPassword()).isNotEmpty();
    }

    @Test(expected = ExecutionException.class)
    public void createDroneWithoutOrganisationToken() throws ExecutionException, InterruptedException {

        Drone newDrone = new Drone();
        newDrone.setName("new drone");
        newDrone.setPayload(500);

        JsonNode jsonDrone = Json.newObject()
                .put("name", newDrone.getName())
                .put("payload", String.valueOf(newDrone.getPayload()));

        JsonNode wrapper = Json.newObject()
                .set("drone", jsonDrone);


        CompletionStage<JsonNode> r = ws.url(BASE_URL + routes.DronesApiController.create().url())
                .setContentType("application/json")
                .setHeader("Accept", "application/json")
                .post(wrapper)
                .thenApply(WSResponse::asJson);

        Json.fromJson(r.toCompletableFuture().get(), Drone.class);
    }

    @Test(expected = ExecutionException.class)
    public void createDroneWithoutName() throws ExecutionException, InterruptedException {

        Drone newDrone = new Drone();
        newDrone.setPayload(500);

        JsonNode jsonDrone = Json.newObject()
                .put("payload", String.valueOf(newDrone.getPayload()))
                .put("organisationToken", "AHSFNASNAHSDF");

        JsonNode wrapper = Json.newObject()
                .set("drone", jsonDrone);

        CompletionStage<JsonNode> r = ws.url(BASE_URL + routes.DronesApiController.create().url())
                .setContentType("application/json")
                .setHeader("Accept", "application/json")
                .post(wrapper)
                .thenApply(WSResponse::asJson);

        Json.fromJson(r.toCompletableFuture().get(), Drone.class);
    }

}
