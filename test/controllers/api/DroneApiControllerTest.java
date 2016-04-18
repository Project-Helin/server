package controllers.api;

import ch.helin.messages.dto.message.DroneDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import models.Drone;
import models.Organisation;
import models.User;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class DroneApiControllerTest extends AbstractIntegrationTest {
    @Inject
    WSClient ws;

    @Inject
    private ApiHelper apiHelper;

    String baseUrl = "http://localhost:19001";

    @Test
    public void createDrone() throws ExecutionException, InterruptedException {
        User user = testHelper.createUserWithOrganisation("bla");

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
        assertThat(returnedDrone.getName(), equalTo(newDrone.getName()));
        assertThat(returnedDrone.getPayload(), equalTo(newDrone.getPayload()));
        assertNotNull(returnedDrone.getOrganisationToken());
        assertNotNull(returnedDrone.getToken());
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


        CompletionStage<JsonNode> r = ws.url(baseUrl + routes.DronesApiController.create().url())
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

        CompletionStage<JsonNode> r = ws.url(baseUrl + routes.DronesApiController.create().url())
                .setContentType("application/json")
                .setHeader("Accept", "application/json")
                .post(wrapper)
                .thenApply(WSResponse::asJson);

        Json.fromJson(r.toCompletableFuture().get(), Drone.class);
    }

}
