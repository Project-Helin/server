package commons;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.*;

import java.io.IOException;

/**
 * Convert object from WKT without SRID
 */
public class JsonGeometryDeserializer<T extends Geometry> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser parser,
                             DeserializationContext ctxt) throws IOException {

        String stringValue = parser.readValueAs(String.class);

        CoordinateReferenceSystem<?> referenceSystem = getSrid4326();
        Geometry<?> geometry = Wkt.fromWkt(stringValue, referenceSystem);
        return (T) geometry;
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
