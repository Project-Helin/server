package commons;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import commons.GisHelper;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;

import java.io.IOException;

/**
 * Convert Point to WKT format - without SRID
 */
public class JsonGeometrySerializer<T extends Geometry> extends JsonSerializer<T> {

    @Override
    public void serialize(T value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        String wktWithoutSrid = GisHelper.toWktStringWithoutSrid(value);
        gen.writeString(wktWithoutSrid);
    }
}
