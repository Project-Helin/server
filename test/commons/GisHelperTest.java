package commons;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.Assertions.assertThat;

public class GisHelperTest {

    private static final Logger logger = LoggerFactory.getLogger(GisHelperTest.class);

    @Test
    public void shouldConvertPointToWkt() {
        Geometry<?> geometry = Wkt.fromWkt("POINT (30 10)");
        logger.info("{}", geometry);

        String wkt = GisHelper.toWktStringWithoutSrid(geometry);
        assertThat(wkt).isEqualTo("POINT(30 10)");
    }
}