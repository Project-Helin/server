package commons;

import ch.helin.messages.commons.AssertUtils;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkt;

/**
 * Contains common static methods related to post gis
 */
public class GisHelper {
    private GisHelper() {
    }

    /*
     * Converts the point to WKT without the SRID information
     * ( SRID = Spatial Reference Identifier )
     */
    public static String toWktStringWithoutSrid(Geometry<?> geometry) {
        AssertUtils.throwExceptionIfNull(geometry);

        String wktWith = Wkt.toWkt(geometry);
        return wktWith.replace("SRID=4326;", "");
    }


    /**
     * Create point from longitude and latitude using WGS-84
     * ( see http://spatialreference.org/ref/epsg/wgs-84/ )
     */
    public static Point createPoint(long longitude, long latitude) {
        Geometry<?> geometry = Wkt.fromWkt("SRID=4326; POINT (" + longitude + " " + latitude + ")" );
        return (Point) geometry;
    }
}
