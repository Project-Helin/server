package commons;

import com.fasterxml.jackson.databind.JsonNode;
import models.Project;
import org.geolatte.geom.Point;
import org.junit.Ignore;
import org.junit.Test;
import play.api.ApplicationLoader;
import play.libs.Json;
import play.test.Helpers;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class CustomApplicationLoaderTest {

    @Test
    @Ignore // do this with zone
    public void shouldParseJsonCorrectly(){
        /**
         * Yeah - just trigger it by hand using dummy values
         */
        new CustomApplicationLoader().builder(new ApplicationLoader.Context(null, null, null, null));

        Point point = GisHelper.createPoint(30, 10);

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("First Demo");

        JsonNode string = Json.toJson(project);
        String jsonRaw = string.toString();
        assertThat(jsonRaw).contains(GisHelper.toWktStringWithoutSrid(point));
    }
}