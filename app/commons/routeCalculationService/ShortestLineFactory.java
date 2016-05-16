package commons.routeCalculationService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.gis.GisHelper;
import org.geolatte.geom.jts.JTS;

/**
 * Created by styp on 16.05.16.
 */
public class ShortestLineFactory {

    public org.geolatte.geom.LineString calculateShortestLineToPoint(org.geolatte.geom.Geometry geometryObject, org.geolatte.geom.Point point){
        Point jtsPoint = (Point) JTS.to(point);
        Geometry jtsGeometry = JTS.to(geometryObject);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsGeometry, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString jtsResultlineString = geometryFactory.createLineString(coordinates);

        return (org.geolatte.geom.LineString) JTS.from(jtsResultlineString, GisHelper.getReferenceSystem());
    }

}
