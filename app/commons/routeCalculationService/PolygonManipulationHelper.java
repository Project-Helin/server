package commons.routeCalculationService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;

import java.util.Arrays;
import java.util.List;

public class PolygonManipulationHelper {

    public static List<Coordinate> degeneratePolygonToPointList (Polygon polygon){
        Geometry jtsGeom = JTS.to(polygon);
        Coordinate[] coordinates = jtsGeom.getCoordinates();
        return Arrays.asList(coordinates);
    }

}
