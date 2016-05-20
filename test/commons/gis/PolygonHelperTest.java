package commons.gis;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;
import org.junit.Test;

import static org.junit.Assert.*;

public class PolygonHelperTest {

    @Test
    public void polygonType() {
        String polygonTypeString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Geometry polygonType = GisHelper.convertFromWktToGeometry(polygonTypeString);

        assertTrue(PolygonHelper.isTypePolygon(polygonType));

        String pointTypeString = "POINT (30 10)";
        Geometry pointType = GisHelper.convertFromWktToGeometry(pointTypeString);

        assertFalse(PolygonHelper.isTypePolygon(pointType));
    }

    @Test
    public void invalidPolygon() {

        /**
         * An invalid polygon is one with a self intersection (example):
         *
         *    ####
         *    #  #
         * #######
         * #  #
         * ####
         */
        String polygonString = "POLYGON((-1 -1, -1 0, 1 0, 1 1, 0 1, 0 -1, -1 -1))";
        Polygon invalidPolygon = GisHelper.convertFromWktToGeometry(polygonString);

        assertFalse(PolygonHelper.isPolygonValid(invalidPolygon));

        polygonString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Polygon validPolygon = (Polygon) GisHelper.convertFromWktToGeometry(polygonString);

        assertTrue(PolygonHelper.isPolygonValid(validPolygon));
    }

    /**
     * Checks if the polygon has no interior ring
     * <p>
     * #############
     * #############
     * #############
     * ###       ###
     * ###       ###
     * ###       ###
     * #############
     * #############
     * #############
     */

    @Test
    public void noInteriorRing() {
        String polygonWithInnerRingString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))";
        Polygon polygonWithInteriorRing = (Polygon) GisHelper.convertFromWktToGeometry(polygonWithInnerRingString);

        assertFalse(PolygonHelper.hasNoInteriorRing(polygonWithInteriorRing));

        String pointTypeString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Polygon pointType = (Polygon) GisHelper.convertFromWktToGeometry(pointTypeString);

        assertTrue(PolygonHelper.hasNoInteriorRing(pointType));
    }

}