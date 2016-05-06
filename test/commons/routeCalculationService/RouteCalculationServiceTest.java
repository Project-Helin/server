package commons.routeCalculationService;

import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import com.google.inject.Inject;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.AbstractIntegrationTest;
import commons.dijkstra.Dijkstra;
import commons.gis.GisHelper;
import dao.ProjectsDao;
import dao.RouteDao;
import models.Project;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.*;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.geom.jts.JTS;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class RouteCalculationServiceTest extends AbstractIntegrationTest {

    @Inject
    private RouteDao routeDao;

    @Inject
    private RouteCalculationService routeCalculationService;

    private CoordinateReferenceSystem<?> wgs84ReferenceSystem =
            CrsRegistry.getCoordinateReferenceSystemForEPSG(4326, CoordinateReferenceSystems.PROJECTED_2D_METER);


    @Test
    public void initTest(){


        List<Waypoint> waypointList = new ArrayList<>();

        Position startPosition = new Position(8.8153890, 47.2237834);
        Position endPosition = new Position(8.8164874, 47.2235279);


        Zone zone = new Zone();
        org.geolatte.geom.Geometry geometry = GisHelper.convertFromWkbToGeometry("0103000020E6100000010000000F000000FFB" +
                "E7D4109A2214002A052D59E9C474031D6A58B22A2214091F301CF999C4740DBD8E020E4A121401D871466909C47403720F1E" +
                "8D2A1214051F0BB048D9C4740305A7EEBC4A12140B1324EBD8B9C474033FF231A9DA1214091AF6CC7939C474076EC7CBC82A" +
                "12140510DF2439A9C4740ABA84A3574A1214039F1A836A29C4740E98D44AA7AA12140979DDE81A69C47409C77BF0B80A12140" +
                "74325B54A79C47401FC639C3C9A1214075CB0D85969C474086992F86D4A121403483A03D959C4740248CACC0D7A121408CC91" +
                "7B5979C4740248CACC0D7A121408CC917B5979C4740FFBE7D4109A2214002A052D59E9C4740");

        zone.setPolygon((Polygon) geometry);
        zone.setName("testpolygon");
        zone.setHeight(3);
        zone.setType(ZoneType.FlightZone);

        Project project = testHelper.createNewProject(testHelper.createNewOrganisation(), zone);

        jpaApi.withTransaction(() ->{
            RouteDto route = routeCalculationService.calculateRoute(startPosition, endPosition, project);
        });


    }

    @Test
    public void shortestPathFromPointToLine(){

        //Test to understand how it works with JTS

        Point point = (Point) GisHelper.convertFromWktToGeometry("POINT(1 1)");
        LineString line = (LineString) GisHelper.convertFromWktToGeometry("LINESTRING(0 0, 0 2)");

        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(point);
        com.vividsolutions.jts.geom.LineString jtsLineString = (com.vividsolutions.jts.geom.LineString) JTS.to(line);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsPoint, jtsLineString);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString jtsResultlineString = geometryFactory.createLineString(coordinates);

        LineString resultLineString = (LineString) JTS.from(jtsResultlineString, wgs84ReferenceSystem);

        assertEquals("SRID=4326;LINESTRING(1 1,0 1)", resultLineString.toString());
    }

    @Test
    public void shortestPathFromPointToMultiLineString(){

        Point point = (Point) GisHelper.convertFromWktToGeometry("POINT(1 1)");
        MultiLineString line = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 2), (0 2, 0 4))");

        LineString lineString = routeCalculationService.calculateShortestLineToPoint(line, point);

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

        LineString lineString = routeCalculationService.calculateShortestLineToPoint(multiLineStringGeom, point);

        assertEquals("SRID=4326;LINESTRING(8.81598105470067 47.2234210272835,8.815975 47.223793)", lineString.toString());

    }



    ///COMMENT THIS SHIT!
    @Test
    public void noPathSplitNeeded(){

        LineString line = (LineString) GisHelper.convertFromWktToGeometry("LINESTRING(0 1,1 1)");
        MultiLineString path = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 1), (0 1, 0 2))");

        assertTrue(routeCalculationService.isLineSplitNeeded(line, path));

    }


    @Test
    public void pathSplitNeeded(){

        LineString line = (LineString) GisHelper.convertFromWktToGeometry("LINESTRING(0 1,1 1)");
        MultiLineString path = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 2), (0 2, 0 4))");

        assertFalse(routeCalculationService.isLineSplitNeeded(line, path));

    }


}