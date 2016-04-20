package commons.gis;

import ch.helin.messages.commons.AssertUtils;
import org.geolatte.geom.ByteBuffer;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkb;
import org.geolatte.geom.codec.WkbDecoder;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.jts.JTS;

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
        Geometry<?> geometry = Wkt.fromWkt("SRID=4326; POINT (" + longitude + " " + latitude + ")");
        return (Point) geometry;
    }

    /**
     *
     * @param wkbString - a WellKnownBinary String
     * @return GeoLatte.geometry - parsed to a simple WKT format
     */

    public static Geometry convertFromWkbToGeometry(String wkbString) {
        if (wkbString == null) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.from(wkbString);
        WkbDecoder decoder = Wkb.newDecoder(Wkb.Dialect.POSTGIS_EWKB_1);
        return decoder.decode(buffer);
    }

    /**
     *
     * @param wktString - a WellKnownBinary String
     * @return GeoLatte.geometry - parsed to a simple WKT format
     */

    public static Geometry convertFromWktToGeometry(String wktString) {
        if (wktString == null) {
            return null;
        }
        return Wkt.fromWkt(wktString);
    }

}
