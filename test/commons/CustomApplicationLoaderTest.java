package commons;

import com.fasterxml.jackson.databind.JsonNode;
import models.Project;
import org.geolatte.geom.Point;
import org.junit.Test;
import play.api.ApplicationLoader;
import play.libs.Json;
import play.test.Helpers;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class CustomApplicationLoaderTest {

    @Test
    public void shouldParseJsonCorrectly(){
        /**
         * Yeah - just trigger it by hand using dummy values
         */
        new CustomApplicationLoader().builder(new ApplicationLoader.Context(null, null, null, null));

        Point point = GisHelper.createPoint(30, 10);

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setHeadquarterPosition(point);
        project.setName("First Demo");

        JsonNode string = Json.toJson(project);
        String jsonRaw = string.toString();
        assertThat(jsonRaw).contains(GisHelper.toWktStringWithoutSrid(point));

        // should parse it back
        JsonNode parsedBack = Json.parse(jsonRaw);
        Project parsedBackPoint = Json.fromJson(parsedBack, Project.class);
        assertThat(parsedBackPoint.getHeadquarterPosition()).isEqualTo(point);
    }
}