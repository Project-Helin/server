package commons.gis;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssertPolygonTest {

    @Test
    public void polygonType() {
        String polygonTypeString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Geometry polygonType = GisHelper.convertFromWktToGeometry(polygonTypeString);

        assertTrue(AssertPolygon.isTypePolygon(polygonType));

        String pointTypeString = "POINT (30 10)";
        Geometry pointType = GisHelper.convertFromWktToGeometry(pointTypeString);

        assertFalse(AssertPolygon.isTypePolygon(pointType));
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
        Polygon invalidPolygon = (Polygon) GisHelper.convertFromWktToGeometry(polygonString);

        assertFalse(AssertPolygon.isPolygonValid(invalidPolygon));

        polygonString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Polygon validPolygon = (Polygon) GisHelper.convertFromWktToGeometry(polygonString);

        assertTrue(AssertPolygon.isPolygonValid(validPolygon));
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

        assertFalse(AssertPolygon.hasNoInteriorRing(polygonWithInteriorRing));

        String pointTypeString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Polygon pointType = (Polygon) GisHelper.convertFromWktToGeometry(pointTypeString);

        assertTrue(AssertPolygon.hasNoInteriorRing(pointType));
    }

}