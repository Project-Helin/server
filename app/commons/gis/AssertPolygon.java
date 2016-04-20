package commons.gis;

import  org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryType;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;

public class AssertPolygon {

    //5m safety margin required!
    public static final double POLYGON_SAFETY_MARGIN = 5;

    public boolean hasPolygonEnoughBoundary(Polygon polygon){
        //com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(polygon);

        //Todo: Check, that Polygon remains a single instance after shrinking with ST_Buffer(obj, 5m)!
        return true;
    }

    public static boolean isPolygonValid(Polygon polygon){
        com.vividsolutions.jts.geom.Geometry jtsPolygon = JTS.to(polygon);
        return jtsPolygon.isValid();
    }

    public static boolean hasNoInteriorRing(Polygon polygon){
        com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(polygon);
        return jtsPolygon.getNumInteriorRing() == 0;
    }

    public static boolean isTypePolygon(Geometry geometry) {
        return (geometry.getGeometryType() == GeometryType.POLYGON);
    }
}
