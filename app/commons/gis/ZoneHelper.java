package commons.gis;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.*;
import org.geolatte.geom.jts.JTS;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ZoneHelper {

    public static void assertAllConstraintsOrThrowRuntimeException(Set<Zone> zones) throws RuntimeException{
        if(!assertOneLoadingZone(zones)){
            throw new RuntimeException("There should be one loading zone.");
        }
        if(!assertMoreThanOneDeliveryZone(zones)){
            throw new RuntimeException("There should be more than one delivery zone.");
        }
        if(!assertOneOrderZone(zones)){
            throw new RuntimeException("There should be at least on order zone.");
        }
        if(!assertAllZonesInisideOrderZone(zones)){
            throw new RuntimeException("All zones are not inside order zone.");
        }
        if(!assertAllZonesAreConnected(zones)){
            throw new RuntimeException("All zones are not connected.");
        }
    }

    public static void asserThatDroneIsInLoadingZoneOrThrowRundTimeException(Set<Zone> zones, org.geolatte.geom.Point dronePoint){
        if(!assertThatDroneIsInLoadingZone(zones, dronePoint)){
            throw new RuntimeException("Drone should be inside the loading zone.");
        }
    }

    public static boolean assertOneLoadingZone(Set<Zone> zones){
        int numOfLoadingZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.LoadingZone).count();
        return (numOfLoadingZones == 1);
    }

    public static boolean assertMoreThanOneDeliveryZone(Set<Zone> zones){
        int numOfDeliveryZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.DeliveryZone).count();
        return (numOfDeliveryZones >= 1);
    }

    public static boolean assertOneOrderZone(Set<Zone> zones){
        int numOfOrderZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.OrderZone).count();
        return (numOfOrderZones == 1);
    }

    // TODO rename this: checkXXX
    public static boolean assertAllZonesInisideOrderZone(Set<Zone> zones){
        Zone orderZone = zones.stream().filter(x -> x.getType() == ZoneType.OrderZone).findFirst().get();
        int numOfZonesInside = (int) zones.stream()
                .filter(x -> x.getType() != ZoneType.OrderZone)
                .filter(x -> JTS.to(orderZone.getPolygon())
                .contains(JTS.to(x.getPolygon())))
                .count();

        //zones - 1 because the order zone should not be contained by itself.
        return ((zones.size() - 1) == numOfZonesInside);
    }

    // If all zones are connected, a solution can be generated.
    // No isle problems! order zone not included ;)
    public static boolean assertAllZonesAreConnected(Set<Zone> zones){

        List<Polygon> polygonList = zones.stream()
                .filter(x -> x.getType() != ZoneType.OrderZone)
                .map(x -> convertZoneToPolygon(x))
                .collect(Collectors.toList());

        Geometry unionedPolygons = CascadedPolygonUnion.union(polygonList);

        return unionedPolygons.getGeometryType().equals("Polygon");
    }


    private static Polygon convertZoneToPolygon(Zone x) {
        return (Polygon) JTS.to(x.getPolygon());
    }


    public static boolean assertThatDroneIsInLoadingZone(Set<Zone> zones, org.geolatte.geom.Point dronePoint){
        Zone loadingZone = zones.stream().filter(x -> x.getType() == ZoneType.LoadingZone).findFirst().get();

        return JTS.to(loadingZone.getPolygon()).contains(JTS.to(dronePoint));
    }

    public static boolean isCustomerInsideDeliveryZone(Set<Zone> zones, org.geolatte.geom.Point customerPoint){
        return false;

    }

}
