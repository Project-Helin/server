package service.routeCalculationService;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.linearref.LinearGeometryBuilder;

import java.util.Arrays;

public class JtsObjectManipulator {

    public static LineString pointsToLineString(Point ... points){
        LinearGeometryBuilder linearGeometryBuilder = new LinearGeometryBuilder(new GeometryFactory());
        for (Point point : points) {
            linearGeometryBuilder.add(point.getCoordinate());
        }

        return (LineString)  linearGeometryBuilder.getGeometry();
    }

    public static boolean isPointOnLineVertex(Point jtsPoint, MultiLineString jtsMultiLineString){
        Coordinate[] coordinates = jtsMultiLineString.getCoordinates();
        return Arrays.asList(coordinates).contains(jtsPoint.getCoordinate());
    }

}
