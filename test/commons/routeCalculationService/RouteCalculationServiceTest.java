package commons.routeCalculationService;

import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.Route;
import ch.helin.messages.dto.way.Waypoint;
import commons.AbstractE2ETest;
import commons.gis.GisHelper;
import models.Zone;
import org.geolatte.geom.Polygon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class RouteCalculationServiceTest extends AbstractE2ETest {

    @Test
    public void initTest(){
        List<Waypoint> waypointList = new ArrayList<>();

        Waypoint startPoint = new Waypoint();
        Waypoint dropPoint = new Waypoint();

        startPoint.setAction(Action.TAKEOFF);
        startPoint.setPosition(new Position(8.8153890, 47.2237834));

        dropPoint.setAction(Action.DROP);
        startPoint.setPosition(new Position(8.8164874, 47.2235279));

        waypointList.add(startPoint);
        waypointList.add(dropPoint);


        Zone zone = new Zone();
        org.geolatte.geom.Geometry geometry = GisHelper.convertFromWkbToGeometry("0103000020E6100000010000000F000000FFB" +
                "E7D4109A2214002A052D59E9C474031D6A58B22A2214091F301CF999C4740DBD8E020E4A121401D871466909C47403720F1E" +
                "8D2A1214051F0BB048D9C4740305A7EEBC4A12140B1324EBD8B9C474033FF231A9DA1214091AF6CC7939C474076EC7CBC82A" +
                "12140510DF2439A9C4740ABA84A3574A1214039F1A836A29C4740E98D44AA7AA12140979DDE81A69C47409C77BF0B80A12140" +
                "74325B54A79C47401FC639C3C9A1214075CB0D85969C474086992F86D4A121403483A03D959C4740248CACC0D7A121408CC91" +
                "7B5979C4740248CACC0D7A121408CC917B5979C4740FFBE7D4109A2214002A052D59E9C4740");

        zone.setPolygon((Polygon) geometry);
        zone.setName("testpolygon");

        List<Zone> zoneList = new ArrayList<>();
        zoneList.add(zone);


        RouteCalculationService routeCalculationService = new RouteCalculationService();
        Route route = routeCalculationService.calculateRoute(waypointList, startPoint, zoneList);
    }





}