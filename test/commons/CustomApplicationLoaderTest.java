package commons;

import com.fasterxml.jackson.databind.JsonNode;
import commons.gis.GisHelper;
import dto.api.ZoneApiDto;
import org.geolatte.geom.Polygon;
import org.junit.Test;
import play.api.ApplicationLoader;
import play.libs.Json;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class CustomApplicationLoaderTest {

    private TestHelper testHelper = new TestHelper();

    @Test
    public void shouldParseJsonCorrectly(){
        /**
         * Since the mapper registered in a singleton,
         * we need to trigger it by hand using dummy values
         */
        new CustomApplicationLoader().builder(new ApplicationLoader.Context(null, null, null, null));

        Polygon polygon = testHelper.createSamplePolygon();

        ZoneApiDto zoneApiDto = new ZoneApiDto();
        zoneApiDto.setId(UUID.randomUUID());
        zoneApiDto.setHeight(10);
        zoneApiDto.setPolygon(polygon);

        JsonNode string = Json.toJson(zoneApiDto);
        String jsonRaw = string.toString();

        /**
         * That polygon should be included ask WKT in the JSON
         */
        assertThat(jsonRaw).contains(GisHelper.toWktStringWithoutSrid(polygon));
    }
}