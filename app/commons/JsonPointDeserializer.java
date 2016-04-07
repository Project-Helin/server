package commons;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.*;

import java.io.IOException;

/**
 * Convert object from WKT without SRID
 */
public class JsonPointDeserializer extends JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonParser parser,
                             DeserializationContext ctxt) throws IOException, JsonProcessingException {

        String stringValue = parser.readValueAs(String.class);

        CoordinateReferenceSystem<?> referenceSystem = getSrid4326();
        Geometry<?> geometry = Wkt.fromWkt(stringValue, referenceSystem);
        return (Point) geometry;
    }

    /**
     * We need this, so that the generated Geometry has the same reference system as the original value.
     */
    private CoordinateReferenceSystem<?> getSrid4326() {
        /**
         * Don't ask me why we need it to do so.
         * I just copied from:
         * org.geolatte.geom.codec.PostgisWktDecoder#prepare(java.lang.String, org.geolatte.geom.crs.CoordinateReferenceSystem)
         */
        return CrsRegistry.getCoordinateReferenceSystemForEPSG(4326, CoordinateReferenceSystems.PROJECTED_2D_METER);
    }
}
