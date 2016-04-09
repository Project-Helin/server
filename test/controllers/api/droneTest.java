package controllers.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSClient;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;

public class droneTest extends AbstractIntegrationTest {
    @Inject
    WSClient ws;

    String baseUrl = "http://localhost:19001";

    @Test
    public void createDrone() throws ExecutionException, InterruptedException {
        ObjectNode drone = Json.newObject();
        ObjectNode repo = Json.newObject();
        repo.put("name", "new drone");
        repo.put("payload", "500");
        repo.put("organisationCode", "AHSFNASNAHSDF");
        drone.put("drone", repo);

        System.out.println("----------------------------------------------");
        System.out.println(drone);

        CompletionStage<String> r = ws.url(baseUrl + routes.DronesController.create().url()).post(drone).thenApply(response -> response.asJson().toString()

        );

        assertThat(r.toCompletableFuture().get(), true);


    }

}
