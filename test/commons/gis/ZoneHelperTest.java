package service.gis;

import models.Zone;
import models.ZoneType;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ZoneHelperTest{

    @Test
    public void testAssertOneLoadingZone(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("loadingZone1");
        zone.setType(ZoneType.LoadingZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        assertTrue(ZoneHelper.checkOneLoadingZone(zoneSet));
    }

    @Test
    public void testAssertOneLoadingZoneFail(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("loadingZone1");
        zone.setType(ZoneType.LoadingZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        Zone zone2 = new Zone();
        zone2.setHeight(5);
        zone2.setName("loadingZone2");
        zone2.setType(ZoneType.LoadingZone);
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        zoneSet.add(zone2);

        assertFalse(ZoneHelper.checkOneLoadingZone(zoneSet));
    }

    @Test
    public void testAssertMoreThanOneDeliveryZoneFail(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("notDeliveryZone");
        zone.setType(ZoneType.LoadingZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        assertFalse(ZoneHelper.checkMoreThanOneDeliveryZone(zoneSet));
    }

    @Test
    public void testAssertMoreThanOneDeliveryOne(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("ThisIsTheDeliveryZone");
        zone.setType(ZoneType.DeliveryZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        assertTrue(ZoneHelper.checkMoreThanOneDeliveryZone(zoneSet));
    }



    @Test
    public void testAssertOneOrderZone(){

        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("OrderZone1");
        zone.setType(ZoneType.OrderZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        assertTrue(ZoneHelper.checkOneOrderZone(zoneSet));

    }

    @Test
    public void testAssertOneOrderZoneToMany(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("OrderZone2");
        zone.setType(ZoneType.OrderZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        Zone zone2 = new Zone();
        zone2.setHeight(5);
        zone2.setName("OrderZone2");
        zone2.setType(ZoneType.OrderZone);
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        zoneSet.add(zone2);

        assertFalse(ZoneHelper.checkOneLoadingZone(zoneSet));
    }

    @Test
    public void testAssertNonOverlappingZones(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("DeliveryZone");
        zone.setType(ZoneType.DeliveryZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        Zone zone2 = new Zone();
        zone2.setHeight(5);
        zone2.setName("FlightZone");
        zone2.setType(ZoneType.FlightZone);
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((130 110, 140 140, 120 140, 110 120, 130 110))"));
        zoneSet.add(zone2);

        assertFalse(ZoneHelper.checkAllZonesAreConnected(zoneSet));
    }

    @Test
    public void testAssertOverlappingZones(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone = new Zone();
        zone.setHeight(0);
        zone.setName("DeliveryZone");
        zone.setType(ZoneType.DeliveryZone);
        zone.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));

        zoneSet.add(zone);

        Zone zone2 = new Zone();
        zone2.setHeight(5);
        zone2.setName("FlightZone");
        zone2.setType(ZoneType.FlightZone);
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON ((20 10, 140 140, 120 140, 110 120, 20 10))"));
        zoneSet.add(zone2);

        assertTrue(ZoneHelper.checkAllZonesAreConnected(zoneSet));
    }

    @Test
    public void assertAllZonesAreInsideOrderZone(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone1 = new Zone();
        zone1.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81475722417235 47.2227524030925," +
                                                                                "8.81587838754058 47.2230657211402," +
                                                                                "8.81714439019561 47.2225921001194," +
                                                                                "8.81519174203276 47.2226321759081," +
                                                                                "8.81475722417235 47.2227524030925))"));
        zone1.setType(ZoneType.FlightZone);
        zoneSet.add(zone1);

        Zone zone2 = new Zone();
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81499862298369 47.2233571781269," +
                                                                                "8.81601786240935 47.2238890829968," +
                                                                                "8.81672060117125 47.2233681077327," +
                                                                                "8.8156477175653 47.2227888355187," +
                                                                                "8.81519954771373 47.2231482866579," +
                                                                                "8.81499862298369 47.2233571781269))"));
        zone2.setType(ZoneType.FlightZone);
        zoneSet.add(zone2);

        Zone zone3 = new Zone();
        zone3.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81635582074523 47.2233936101375," +
                                                                                "8.81672060117126 47.2231604448367," +
                                                                                "8.81711756810546 47.2234263989257," +
                                                                                "8.81675278767943 47.2236231312289," +
                                                                                "8.81635582074523 47.2233936101375))"));
        zone3.setType(ZoneType.LoadingZone);
        zoneSet.add(zone3);

        Zone zone4 = new Zone();
        zone4.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.8152713701129 47.2233280325003," +
                                                                                "8.81405901163816 47.224020236802," +
                                                                                "8.81329726427794 47.2233207460911," +
                                                                                "8.81396245211363 47.22282526792," +
                                                                                "8.81550740450621 47.2231677312678," +
                                                                                "8.8152713701129 47.2233280325003))"));
        zone4.setType(ZoneType.FlightZone);
        zoneSet.add(zone4);


        Zone zone5 = new Zone();
        zone5.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81394183263182 47.2229527811272," +
                                                                                "8.81510591134429 47.2227742625513," +
                                                                                "8.81428515538573 47.2221949838485," +
                                                                                "8.81305670365691 47.2226540354165," +
                                                                                "8.81394183263182 47.2229527811272))"));
        zone5.setType(ZoneType.FlightZone);
        zoneSet.add(zone5);

        Zone zone6 = new Zone();
        zone6.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.8117952272296 47.2243918374882," +
                                                                                "8.8117415830493 47.2219581699775," +
                                                                                "8.81795357912779 47.2220456090684," +
                                                                                "8.81727766245604 47.2243991237501," +
                                                                                "8.8117952272296 47.2243918374882))"));
        zone6.setType(ZoneType.OrderZone);
        zoneSet.add(zone6);


        Zone zone7 = new Zone();
        zone7.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81283424794674 47.2228617002962," +
                                                                                "8.81375692784786 47.2227013976536," +
                                                                                "8.8126840442419 47.2222350599374," +
                                                                                "8.81201885640621 47.2225993866287," +
                                                                                "8.81283424794674 47.2228617002962))"));
        zone7.setType(ZoneType.DeliveryZone);
        zoneSet.add(zone7);

        assertTrue(ZoneHelper.checkAllZonesInsideOrderZone(zoneSet));


    }

    @Test
    public void assertAllZonesAreInsideOrderZoneDeliveryZoneIsOut(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone1 = new Zone();
        zone1.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81475722417235 47.2227524030925," +
                "8.81587838754058 47.2230657211402," +
                "8.81714439019561 47.2225921001194," +
                "8.81519174203276 47.2226321759081," +
                "8.81475722417235 47.2227524030925))"));
        zone1.setType(ZoneType.FlightZone);
        zoneSet.add(zone1);

        Zone zone2 = new Zone();
        zone2.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81499862298369 47.2233571781269," +
                "8.81601786240935 47.2238890829968," +
                "8.81672060117125 47.2233681077327," +
                "8.8156477175653 47.2227888355187," +
                "8.81519954771373 47.2231482866579," +
                "8.81499862298369 47.2233571781269))"));
        zone2.setType(ZoneType.FlightZone);
        zoneSet.add(zone2);

        Zone zone3 = new Zone();
        zone3.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81635582074523 47.2233936101375," +
                "8.81672060117126 47.2231604448367," +
                "8.81711756810546 47.2234263989257," +
                "8.81675278767943 47.2236231312289," +
                "8.81635582074523 47.2233936101375))"));
        zone3.setType(ZoneType.LoadingZone);
        zoneSet.add(zone3);

        Zone zone4 = new Zone();
        zone4.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.8152713701129 47.2233280325003," +
                "8.81405901163816 47.224020236802," +
                "8.81329726427794 47.2233207460911," +
                "8.81396245211363 47.22282526792," +
                "8.81550740450621 47.2231677312678," +
                "8.8152713701129 47.2233280325003))"));
        zone4.setType(ZoneType.FlightZone);
        zoneSet.add(zone4);


        Zone zone5 = new Zone();
        zone5.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81394183263182 47.2229527811272," +
                "8.81510591134429 47.2227742625513," +
                "8.81428515538573 47.2221949838485," +
                "8.81305670365691 47.2226540354165," +
                "8.81394183263182 47.2229527811272))"));
        zone5.setType(ZoneType.FlightZone);
        zoneSet.add(zone5);

        Zone zone6 = new Zone();
        zone6.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.8117952272296 47.2243918374882," +
                "8.8117415830493 47.2219581699775," +
                "8.81795357912779 47.2220456090684," +
                "8.81727766245604 47.2243991237501," +
                "8.8117952272296 47.2243918374882))"));
        zone6.setType(ZoneType.OrderZone);
        zoneSet.add(zone6);


        Zone zone7 = new Zone();
        zone7.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81283424794674 47.2228617002962," +
                                                                                "8.81375692784786 47.2227013976536," +
                                                                                "8.8126840442419 47.2222350599374," +
                                                                                "8.81089400500059 47.2228034084823," +
                                                                                "8.81283424794674 47.2228617002962))"));
        zone7.setType(ZoneType.DeliveryZone);
        zoneSet.add(zone7);

        assertFalse(ZoneHelper.checkAllZonesInsideOrderZone(zoneSet));

    }

    @Test
    public void droneShouldBeInSingleDeliveryZone(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone1 = new Zone();
        zone1.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81732141599059 47.223743356166," +
                                                                                "8.81741797551513 47.2239583031011," +
                                                                                "8.81779884919524 47.2237797879112," +
                                                                                "8.81750917062163 47.2236449903287," +
                                                                                "8.81732141599059 47.223743356166))"));
        zone1.setType(ZoneType.DeliveryZone);
        zoneSet.add(zone1);

        Point customerPoint = GisHelper.createPoint(8.81749314556619, 47.2238690455062);

        assertTrue(ZoneHelper.isCustomerInsideDeliveryZone(zoneSet, customerPoint));

    }

    @Test
    public void droneShouldBeOutsideDeliveryZone(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone1 = new Zone();
        zone1.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81732141599059 47.223743356166," +
                                                                                "8.81741797551513 47.2239583031011," +
                                                                                "8.81779884919524 47.2237797879112," +
                                                                                "8.81750917062163 47.2236449903287," +
                                                                                "8.81732141599059 47.223743356166))"));
        zone1.setType(ZoneType.DeliveryZone);
        zoneSet.add(zone1);

        Point customerPoint = GisHelper.createPoint(9.81749314556619, 47.2238690455062);

        assertFalse(ZoneHelper.isCustomerInsideDeliveryZone(zoneSet, customerPoint));

    }

    @Test
    public void customerShouldBeInOrderZone(){
        Set<Zone> zoneSet = new HashSet<>();

        Zone zone1 = new Zone();
        zone1.setPolygon((Polygon) GisHelper.convertFromWktToGeometry("POLYGON((8.81732141599059 47.223743356166," +
                "8.81741797551513 47.2239583031011," +
                "8.81779884919524 47.2237797879112," +
                "8.81750917062163 47.2236449903287," +
                "8.81732141599059 47.223743356166))"));
        zone1.setType(ZoneType.OrderZone);
        zoneSet.add(zone1);

        Point customerPoint = GisHelper.createPoint(8.81749314556619, 47.2238690455062);

        assertTrue(ZoneHelper.checkThatCustomerIsInOrderZone(zoneSet, customerPoint));
    }

}