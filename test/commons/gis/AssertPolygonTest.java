package commons.gis;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssertPolygonTest {

    @Test
    public void polygonType(){
        String polygonTypeString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Geometry polygonType = GisHelper.convertFromWktToGeometry(polygonTypeString);

        assertTrue(AssertPolygon.isTypePolygon(polygonType));

        String pointTypeString = "POINT (30 10)";
        Geometry pointType = GisHelper.convertFromWktToGeometry(pointTypeString);

        assertFalse(AssertPolygon.isTypePolygon(pointType));
    }

    @Test
    public void invalidPolygon(){
        String polygonString = "POLYGON((-1 -1, -1 0, 1 0, 1 1, 0 1, 0 -1, -1 -1))";
        Polygon invalidPolygon = (Polygon) GisHelper.convertFromWktToGeometry(polygonString);

        assertFalse(AssertPolygon.isPolygonValid(invalidPolygon));

        polygonString  = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Polygon validPolygon = (Polygon) GisHelper.convertFromWktToGeometry(polygonString);

        assertTrue(AssertPolygon.isPolygonValid(validPolygon));
    }

    @Test
    public void noInteriorRing(){
        String polygonWithInnerRingString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))";
        Polygon polygonWithInteriorRing = (Polygon) GisHelper.convertFromWktToGeometry(polygonWithInnerRingString);

        assertFalse(AssertPolygon.hasNoInteriorRing(polygonWithInteriorRing));

        String pointTypeString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))";
        Polygon pointType = (Polygon) GisHelper.convertFromWktToGeometry(pointTypeString);

        assertTrue(AssertPolygon.hasNoInteriorRing(pointType));
    }

    @Test
    public void invalidPolygonFolding(){
        String polygonWithInnerRing = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))";
        Geometry geometry = GisHelper.convertFromWktToGeometry(polygonWithInnerRing);

        com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(geometry);
        System.out.println(polygon.getNumInteriorRing());


        geometry = GisHelper.convertFromWkbToGeometry("0103000020E6100000010000000F000000FFBE7D4109A2214002A052D59E9C474031D6A58B22A2214091F301CF999C4740DBD8E020E4A121401D871466909C47403720F1E8D2A1214051F0BB048D9C4740305A7EEBC4A12140B1324EBD8B9C474033FF231A9DA1214091AF6CC7939C474076EC7CBC82A12140510DF2439A9C4740ABA84A3574A1214039F1A836A29C4740E98D44AA7AA12140979DDE81A69C47409C77BF0B80A1214074325B54A79C47401FC639C3C9A1214075CB0D85969C474086992F86D4A121403483A03D959C4740248CACC0D7A121408CC917B5979C4740248CACC0D7A121408CC917B5979C4740FFBE7D4109A2214002A052D59E9C4740");
        com.vividsolutions.jts.geom.Geometry g = JTS.to(geometry);

    }



}