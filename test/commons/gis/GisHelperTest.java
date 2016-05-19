package commons.gis;

import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.Waypoint;
import org.geolatte.geom.ByteBuffer;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkb;
import org.geolatte.geom.codec.Wkt;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class GisHelperTest {

    private static final Logger logger = LoggerFactory.getLogger(GisHelperTest.class);

    @Test
    public void shouldConvertPointToWkt() {
        Geometry<?> geometry = Wkt.fromWkt("SRID=4326; POINT (30 10)");
        logger.info("{}", geometry);

        String wkt = GisHelper.toWktStringWithoutSrid(geometry);
        assertThat(wkt).isEqualTo("POINT(30 10)");
    }

    @Test
    public void shouldConvertWktWithoutSRIDToPosition() {
        String longitude = "8.817091584205626";
        String latitude = "47.22393280096793";
        String wktWithoutSRID = "POINT (" + longitude + " " + latitude + ")";

        Position position = GisHelper.createPosition(wktWithoutSRID);
        assertThat(String.valueOf(position.getLat())).isEqualTo(latitude);
        assertThat(String.valueOf(position.getLon())).isEqualTo(longitude);
    }

    @Test
    public void shouldCreatePoint() {
        Point<?> point = GisHelper.createPoint(100, -150);

        assertThat(point.toString()).isEqualTo("SRID=4326;POINT(100 -150)");
    }

}