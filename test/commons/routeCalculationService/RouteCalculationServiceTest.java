package commons.routeCalculationService;

import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.AbstractIntegrationTest;
import commons.gis.GisHelper;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.geom.jts.JTS;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RouteCalculationServiceTest extends AbstractIntegrationTest {

    @Inject
    private RouteCalculationService routeCalculationService;

    private CoordinateReferenceSystem<?> wgs84ReferenceSystem =
        CrsRegistry.getCoordinateReferenceSystemForEPSG(4326, CoordinateReferenceSystems.PROJECTED_2D_METER);

    @Test
    public void shortestPathFromPointToLine() {
        // Test to understand how it works with JTS
        Point point = GisHelper.convertFromWktToGeometry("POINT(1 1)");
        LineString line = GisHelper.convertFromWktToGeometry("LINESTRING(0 0, 0 2)");

        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(point);
        com.vividsolutions.jts.geom.LineString jtsLineString = (com.vividsolutions.jts.geom.LineString) JTS.to(line);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsPoint, jtsLineString);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString jtsResultlineString = geometryFactory.createLineString(coordinates);

        LineString resultLineString = (LineString) JTS.from(jtsResultlineString, wgs84ReferenceSystem);

        assertEquals("SRID=4326;LINESTRING(1 1,0 1)", resultLineString.toString());
    }

    @Test
    public void splitMultiLineStringBasicCase() {
        Point point = GisHelper.convertFromWktToGeometry("POINT(0 1)");
        MultiLineString path = GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 2), (0 2, 0 4))");

        MultiLineString returnMultiLineString = routeCalculationService.splitMultiLineStringOnPoint(path, point);

        assertEquals("SRID=4326;MULTILINESTRING((0 0,0 1),(0 1,0 2),(0 2,0 4))", returnMultiLineString.toString());
    }

    @Test
    public void splitMultiLineStringWithoutEffect() {
        Point point = GisHelper.convertFromWktToGeometry("POINT(0 1)");
        MultiLineString path = GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 1), (0 1, 0 2))");

        MultiLineString returnMultiLineString = routeCalculationService.splitMultiLineStringOnPoint(path, point);

        assertEquals("SRID=4326;MULTILINESTRING((0 0,0 1),(0 1,0 2))", returnMultiLineString.toString());
    }

    @Test
    public void splitMultiLineStringOutOffBoundsWithoutEffect() {
        Point point = (Point) GisHelper.convertFromWktToGeometry("POINT(0 5)");
        MultiLineString path = (MultiLineString) GisHelper.convertFromWktToGeometry("MULTILINESTRING((0 0, 0 1), (0 1, 0 2))");

        MultiLineString returnMultiLineString = routeCalculationService.splitMultiLineStringOnPoint(path, point);

        assertEquals("SRID=4326;MULTILINESTRING((0 0,0 1),(0 1,0 2))", returnMultiLineString.toString());
    }

    @Test
    public void shouldCheckComperatorInDijkstra() {
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