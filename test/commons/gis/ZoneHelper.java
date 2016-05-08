package commons.gis;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.jts.JTS;

import java.util.List;
import java.util.stream.Collectors;

public class ZoneHelper {

    public static boolean assertOneLoadingZone(List<Zone> zones){
        int numOfLoadingZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.LoadingZone).count();
        return (numOfLoadingZones == 1);
    }

    public static boolean assertMoreThanOneDeliveryZone(List<Zone> zones){
        int numOfDeliveryZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.DeliveryZone).count();
        return (numOfDeliveryZones >= 1);
    }

    public static boolean assertOneOrderZone(List<Zone> zones){
        int numOfOrderZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.OrderZone).count();
        return (numOfOrderZones == 1);
    }

    public static boolean assertAllZonesInisideOrderZone(List<Zone> zones){
        Zone orderZone = zones.stream().filter(x -> x.getType() == ZoneType.OrderZone).findFirst().get();
        int numOfZonesInside = (int) zones.stream().filter(x -> JTS.to(orderZone.getPolygon()).contains(JTS.to(x.getPolygon()))).count();

        //zones - 1 because the order zone should not be contained by itself.
        return ((zones.size() - 1) == numOfZonesInside);
    }

    // If all zones are connected, a solution can be generated.
    // No isle problems! order zone not included ;)
    public static boolean assertAllZonesAreConnected(List<Zone> zones){

        List<Polygon> polygonList = zones.stream()
                .filter(x -> x.getType() != ZoneType.OrderZone)
                .map(x -> convertZoneToPolygon(x))
                .collect(Collectors.toList());

        Geometry unionedPolygons = CascadedPolygonUnion.union(polygonList);

        //Todo: Fix this!
        //This is shit but no idea so far

        System.out.println(unionedPolygons.getGeometryType());
        return unionedPolygons.getGeometryType().equals("POLYGON");
    }

    private static Polygon convertZoneToPolygon(Zone x) {
        return (Polygon) JTS.to(x.getPolygon());
    }
}
