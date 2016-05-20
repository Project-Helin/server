package commons.gis;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryType;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;

public class PolygonHelper {

    public static boolean isPolygonValid(Polygon polygon) {
        com.vividsolutions.jts.geom.Geometry jtsPolygon = JTS.to(polygon);
        return jtsPolygon.isValid();
    }

    public static boolean hasNoInteriorRing(Polygon polygon) {
        com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(polygon);
        return jtsPolygon.getNumInteriorRing() == 0;
    }

    public static boolean isTypePolygon(Geometry geometry) {
        return (geometry.getGeometryType() == GeometryType.POLYGON);
    }
}
