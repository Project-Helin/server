package commons.gis;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.geolatte.geom.jts.JTS;


public class ShortestLineFactory {

    public org.geolatte.geom.LineString calculateShortestLineToPoint(org.geolatte.geom.Geometry geometryObject, org.geolatte.geom.Point point){
        Point jtsPoint = (Point) JTS.to(point);
        Geometry jtsGeometry = JTS.to(geometryObject);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsGeometry, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString jtsResultLineString = geometryFactory.createLineString(coordinates);

        return (org.geolatte.geom.LineString) JTS.from(jtsResultLineString, GisHelper.getReferenceSystem());
    }



}
