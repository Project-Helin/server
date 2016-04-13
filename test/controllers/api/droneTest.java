package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import models.Drone;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class droneTest extends AbstractIntegrationTest {
    @Inject
    WSClient ws;

    String baseUrl = "http://localhost:19001";

    @Test
    public void createDrone() throws ExecutionException, InterruptedException {

        Drone newDrone = new Drone();
        newDrone.setName("new drone");
        newDrone.setPayload(500);

        JsonNode jsonDrone = Json.newObject()
                .put("name", newDrone.getName())
                .put("payload", String.valueOf(newDrone.getPayload()))
                .put("organisationToken", "AHSFNASNAHSDF");

        JsonNode wrapper = Json.newObject()
                .set("drone", jsonDrone);

        CompletionStage<JsonNode> r = ws.url(baseUrl + routes.DronesController.create().url())
                .setContentType("application/json")
                .setHeader("Accept", "application/json")
                .post(wrapper)
                .thenApply(WSResponse::asJson);

        Drone droneObject = Json.fromJson(r.toCompletableFuture().get(), Drone.class);

        assertNotNull(droneObject.getId());
        assertThat(droneObject.getName(), equalTo(newDrone.getName()));
        assertThat(droneObject.getPayload(), equalTo(newDrone.getPayload()));
        assertNotNull(droneObject.getOrganisation());
        assertNotNull(droneObject.getToken());
    }

}
