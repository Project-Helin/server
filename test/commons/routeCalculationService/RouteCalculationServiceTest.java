package commons.routeCalculationService;

import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.Waypoint;
import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.AbstractIntegrationTest;
import commons.gis.GisHelper;
import dao.RouteDao;
import models.*;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.geom.jts.JTS;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class RouteCalculationServiceTest extends AbstractIntegrationTest {

    @Inject
    private RouteDao routeDao;

    @Inject
    private RouteCalculationService routeCalculationService;

    private CoordinateReferenceSystem<?> wgs84ReferenceSystem =
            CrsRegistry.getCoordinateReferenceSystemForEPSG(4326, CoordinateReferenceSystems.PROJECTED_2D_METER);


    @Test
    @Ignore
    //This Test can not be executed at the moment, because of many constraint violating problems.
    //If you are bored, please fix it.
    public void initTest(){

        List<Waypoint> waypointList = new ArrayList<>();

        Position startPosition = new Position(8.8153890, 47.2237834);
        Position endPosition = new Position(8.8164874, 47.2235279);

        Zone zone = new Zone();
        org.geolatte.geom.Geometry geometry =
                GisHelper.convertFromWktToGeometry("POLYGON((8.81647686634051 47.2235972073977," +
                                                            "8.81666981124281 47.2234438666848," +
                                                            "8.81619360680309 47.2231567001565," +
                                                            "8.8160622400611 47.2230535428686," +
                                                            "8.81595550458323 47.2230145103289," +
                                                            "8.81565171899238 47.2232598572438," +
                                                            "8.8154505636687 47.2234578067679," +
                                                            "8.81533972298015 47.2237003636278," +
                                                            "8.81538898550839 47.2238313996306," +
                                                            "8.81543003761526 47.2238564915941," +
                                                            "8.81599245147942 47.2233434979779," +
                                                            "8.81607455569316 47.2233044656518," +
                                                            "8.81609918695728 47.2233797422551," +
                                                            "8.81609918695728 47.2233797422551," +
                                                            "8.81647686634051 47.2235972073977))");

        zone.setPolygon((Polygon) geometry);
        zone.setName("testpolygon");
        zone.setHeight(3);
        zone.setType(ZoneType.FlightZone);

        Project project = testHelper.createNewProject(testHelper.createNewOrganisation(), zone);

        jpaApi.withTransaction(() ->{
            List<Position> positions = routeCalculationService.calculateRoute(startPosition, endPosition, project);
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
    public void splitMultiLineStringBasicCase(){
        Point point = (Point) GisHelper.convertFromWktToGeometry("POINT(0 1)");
        MultiLineString path = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 2), (0 2, 0 4))");

        MultiLineString returnMultiLineString = routeCalculationService.splitMultiLineStringOnPoint(path, point);

        assertEquals("SRID=4326;MULTILINESTRING((0 0,0 1),(0 1,0 2),(0 2,0 4))", returnMultiLineString.toString());
    }

    @Test
    public void splitMultiLineStringWithoutEffect(){
        Point point = (Point) GisHelper.convertFromWktToGeometry("POINT(0 1)");
        MultiLineString path = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 1), (0 1, 0 2))");

        MultiLineString returnMultiLineString = routeCalculationService.splitMultiLineStringOnPoint(path, point);

        assertEquals("SRID=4326;MULTILINESTRING((0 0,0 1),(0 1,0 2))", returnMultiLineString.toString());
    }

    @Test
    public void splitMultiLineStringOutOffBoundsWithoutEffect(){
        Point point = (Point) GisHelper.convertFromWktToGeometry("POINT(0 5)");
        MultiLineString path = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 1), (0 1, 0 2))");

        MultiLineString returnMultiLineString = routeCalculationService.splitMultiLineStringOnPoint(path, point);

        assertEquals("SRID=4326;MULTILINESTRING((0 0,0 1),(0 1,0 2))", returnMultiLineString.toString());
    }

    @Test
    public void shouldCheckComperatorInDijkstra(){
        ArrayList<LineString> helperList = new ArrayList<>();

        LineString line1 = (LineString) GisHelper.convertFromWktToGeometry("LINESTRING(8.81305670365691 47.2226540354165,8.81247818470001 47.22380529011801)");
        helperList.add(line1);

        LineString line2 = (LineString) GisHelper.convertFromWktToGeometry("LINESTRING(8.81305670365691 47.2226540354165,8.81247818470001 47.22380529011801)");
        helperList.add(line2);

        LineString line3 = (LineString) GisHelper.convertFromWktToGeometry("LINESTRING(8.81305670365691 47.2226540354165,8.81396906260474 47.2226393885969)");
        helperList.add(line3);

        UndirectedGraph<org.geolatte.geom.Position, LineString> graph = new SimpleGraph<>(LineString.class);

        for (LineString lineString : helperList) {
            graph.addVertex(lineString.getStartPosition());
            graph.addVertex(lineString.getEndPosition());
            graph.addEdge(lineString.getStartPosition(), lineString.getEndPosition(), lineString);
        }

        // Check, that the 2 same entries are treted as one.
        int size = graph.vertexSet().size();
        assertEquals(3, size);
    }

}