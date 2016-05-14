package dao;

import ch.helin.messages.dto.Action;
import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.TestHelper;
import commons.gis.GisHelper;
import models.*;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;

import org.geolatte.geom.Polygon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;


public class RouteDaoTest extends AbstractIntegrationTest {

    @Inject
    private RouteDao routeDao;

    @Test
    public void name() throws Exception {
        jpaApi.withTransaction(() -> {

            WayPoint bla = new WayPoint();

            Route route = new Route();
            bla.setRoute(route);
            bla.setPosition(GisHelper.createPoint(10, 10));
            bla.setAction(Action.DROP);
            route.setWayPoints(Arrays.asList(bla));

            routeDao.persist(route);
        });
    }

    @Test
    public void shortestPathFromPointToWay() {

        String multiLineString = "MULTILINESTRING((8.81653463424814 47.2233814149235,8.81654876915496 47.2233893148946)," +
            "(8.81653463424814 47.2233814149235,8.81598105470067 47.2234210272835)," +
            "(8.81654876915496 47.2233893148946,8.81671817654337 47.2233855437005)," +
            "(8.81675595809367 47.2234090865688,8.81671817654337 47.2233855437005)," +
            "(8.81533954799537 47.2233014863053,8.81569109819225 47.2232671012872)," +
            "(8.81598105470067 47.2234210272835,8.81569109819225 47.2232671012872))";

        MultiLineString multiLineStringGeom = (MultiLineString) GisHelper.convertFromWktToGeometry(multiLineString);

        String positionString = "POINT(8.815975 47.223793)";
        Point point = (Point) GisHelper.convertFromWktToGeometry(positionString);


        LineString shortestPath = (LineString) jpaApi.withTransaction((em) -> {
            return routeDao.calculateShortestLineToPoint(multiLineStringGeom, point);
        });

        assertThat(shortestPath.toString()).isEqualTo("SRID=4326;LINESTRING(8.81598105470067 47.2234210272835,8.815975 47.223793)");

    }

    @Test
    public void shouldReturnSkeletonAsMultiLineString() {

        String polygonString = "POLYGON((8.81647686634051 47.2235972073977," +
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
            "8.81647686634051 47.2235972073977))";

        Polygon testZonePolygon = (Polygon) GisHelper.convertFromWktToGeometry(polygonString);

        Zone zone = new Zone();
        zone.setHeight(3);
        zone.setName("myTestZone");
        zone.setPolygon(testZonePolygon);
        zone.setType(ZoneType.FlightZone);

        List<Zone> zones = new ArrayList<>();
        zones.add(zone);


        Project testProject = testHelper.createNewProject(testHelper.createNewOrganisation(), zone);

        MultiLineString multiLineResult = jpaApi.withTransaction(() -> {
            return routeDao.calculateSkeleton(testProject.getId());
        });

        String expectedSkeletonLine = "SRID=4326;MULTILINESTRING((8.81542480157503 47.2238017861246,8.81544827593939 47.2237041119867)," +
            "(8.81612055126825 47.2231972919686,8.81614322549452 47.2232296877017)," +
            "(8.81612055126825 47.2231972919686,8.81599026768789 47.2231827764133)," +
            "(8.81614322549452 47.2232296877017,8.81619845658185 47.2232990887558)," +
            "(8.81544827593939 47.2237041119867,8.81556791970555 47.2235400094954)," +
            "(8.81646639433932 47.2234569928832,8.81619845658185 47.2232990887558)," +
            "(8.81591402905597 47.2232267826139,8.81598347117314 47.2231830933168)," +
            "(8.81591402905597 47.2232267826139,8.81574877132104 47.223368661629)," +
            "(8.81556791970555 47.2235400094954,8.81574877132104 47.223368661629)," +
            "(8.81599026768789 47.2231827764133,8.81598347117314 47.2231830933168))";

        assertEquals(expectedSkeletonLine, multiLineResult.toString());

    }


}