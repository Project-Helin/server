package commons.routeCalculationService;

import commons.gis.GisHelper;
import commons.gis.RouteHelper;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShortestLineFactoryTest {


    @Test
    public void shortestPathFromPointToMultiLineString(){

        Point point = (Point) GisHelper.convertFromWktToGeometry("POINT(1 1)");
        MultiLineString line = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 2), (0 2, 0 4))");

        RouteHelper slFactory = new RouteHelper();
        LineString lineString = slFactory.calculateShortestLineToPoint(line, point);

        assertEquals("SRID=4326;LINESTRING(0 1,1 1)", lineString.toString());

    }

    @Test
    public void shortestPathFromPointToWay(){

        String multiLineString =  "MULTILINESTRING((8.81653463424814 47.2233814149235,8.81654876915496 47.2233893148946)," +
                "(8.81653463424814 47.2233814149235,8.81598105470067 47.2234210272835)," +
                "(8.81654876915496 47.2233893148946,8.81671817654337 47.2233855437005)," +
                "(8.81675595809367 47.2234090865688,8.81671817654337 47.2233855437005)," +
                "(8.81533954799537 47.2233014863053,8.81569109819225 47.2232671012872)," +
                "(8.81598105470067 47.2234210272835,8.81569109819225 47.2232671012872))";

        MultiLineString multiLineStringGeom = (MultiLineString) GisHelper.convertFromWktToGeometry(multiLineString);

        String positionString = "POINT(8.815975 47.223793)";
        Point point = (Point) GisHelper.convertFromWktToGeometry(positionString);

        RouteHelper slFactory = new RouteHelper();
        LineString lineString = slFactory.calculateShortestLineToPoint(multiLineStringGeom, point);

        assertEquals("SRID=4326;LINESTRING(8.81598105470067 47.2234210272835,8.815975 47.223793)", lineString.toString());

    }

}