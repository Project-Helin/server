package commons.gis;

import commons.AbstractIntegrationTest;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.Polygon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ZoneHelperTest{

    @Test
    public void testAssertOneLoadingZone(){
        List<Zone> zoneList = new ArrayList<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("loadingZone1");
        zone.setType(ZoneType.LoadingZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneList.add(zone);

        assertTrue(ZoneHelper.assertOneLoadingZone(zoneList));
    }

    @Test
    public void testAssertOneLoadingZoneFail(){
        List<Zone> zoneList = new ArrayList<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("loadingZone1");
        zone.setType(ZoneType.LoadingZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneList.add(zone);

        Zone zone2 = new Zone();
        zone2.setHeight(5);
        zone2.setName("loadingZone2");
        zone2.setType(ZoneType.LoadingZone);
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        zoneList.add(zone2);

        assertFalse(ZoneHelper.assertOneLoadingZone(zoneList));
    }

    @Test
    public void testAssertMoreThanOneDeliveryZoneFail(){
        List<Zone> zoneList = new ArrayList<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("notDeliveryZone");
        zone.setType(ZoneType.LoadingZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneList.add(zone);

        assertFalse(ZoneHelper.assertMoreThanOneDeliveryZone(zoneList));
    }

    @Test
    public void testAssertMoreThanOneDeliveryOne(){
        List<Zone> zoneList = new ArrayList<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("ThisIsTheDeliveryZone");
        zone.setType(ZoneType.DeliveryZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneList.add(zone);

        assertTrue(ZoneHelper.assertMoreThanOneDeliveryZone(zoneList));
    }



    @Test
    public void testAssertOneOrderZone(){

        List<Zone> zoneList = new ArrayList<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("OrderZone1");
        zone.setType(ZoneType.OrderZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneList.add(zone);

        assertTrue(ZoneHelper.assertOneOrderZone(zoneList));

    }

    @Test
    public void testAssertOneOrderZoneToMany(){
        List<Zone> zoneList = new ArrayList<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("OrderZone2");
        zone.setType(ZoneType.OrderZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneList.add(zone);

        Zone zone2 = new Zone();
        zone2.setHeight(5);
        zone2.setName("OrderZone2");
        zone2.setType(ZoneType.OrderZone);
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        zoneList.add(zone2);

        assertFalse(ZoneHelper.assertOneLoadingZone(zoneList));
    }

    @Test
    public void testAssertAllZonesInisideOrderZone() throws Exception {

    }

    @Test
    public void testAssertNonOverlappingZones(){
        List<Zone> zoneList = new ArrayList<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("DeliveryZone");
        zone.setType(ZoneType.DeliveryZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneList.add(zone);

        Zone zone2 = new Zone();
        zone2.setHeight(5);
        zone2.setName("FlightZone");
        zone2.setType(ZoneType.FlightZone);
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((130 110, 140 140, 120 140, 110 120, 130 110))"));
        zoneList.add(zone2);

        assertFalse(ZoneHelper.assertAllZonesAreConnected(zoneList));
    }
}