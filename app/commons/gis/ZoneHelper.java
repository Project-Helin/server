package commons.gis;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.Point;
import org.geolatte.geom.jts.JTS;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ZoneHelper {

    public static void assertAllConstraintsOrThrowRuntimeException(Set<Zone> zones) throws RuntimeException{
        if(!checkOneLoadingZone(zones)){
            throw new RuntimeException("There should be one loading zone.");
        }
        if(!checkMoreThanOneDeliveryZone(zones)){
            throw new RuntimeException("There should be more than one delivery zone.");
        }
        if(!checkOneOrderZone(zones)){
            throw new RuntimeException("There should be at least on order zone.");
        }
        if(!checkAllZonesInisideOrderZone(zones)){
            throw new RuntimeException("All zones are not inside order zone.");
        }
        if(!checkAllZonesAreConnected(zones)){
            throw new RuntimeException("All zones are not connected.");
        }
    }

    public static void assertThatCustomerIsInOrderZoneOrThrowRunTimeException(Set<Zone> zones, org.geolatte.geom.Point customerPoint){
        if(!checkThatCustomerIsInOrderZone(zones, customerPoint)){
            throw new RuntimeException("Customer can't be outside OrderZone.");
        }

    }

    public static void assertThatDroneIsInLoadingZoneOrThrowRunTimeException(Set<Zone> zones, org.geolatte.geom.Point dronePoint){
        if(!checkThatDroneIsInLoadingZone(zones, dronePoint)){
            throw new RuntimeException("Drone should be inside the loading zone.");
        }
    }


    public static boolean checkThatCustomerIsInOrderZone(Set<Zone> zones, Point customerPoint) {
        Polygon orderZonePolygon = zones.stream().filter(x -> x.getType() == ZoneType.OrderZone).map(x -> convertZoneToJtsPolygon(x)).findFirst().get();
        return orderZonePolygon.contains(JTS.to(customerPoint));
    }

    public static boolean checkOneLoadingZone(Set<Zone> zones){
        int numOfLoadingZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.LoadingZone).count();
        return (numOfLoadingZones == 1);
    }

    public static boolean checkMoreThanOneDeliveryZone(Set<Zone> zones){
        int numOfDeliveryZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.DeliveryZone).count();
        return (numOfDeliveryZones >= 1);
    }

    public static boolean checkOneOrderZone(Set<Zone> zones){
        int numOfOrderZones = (int) zones.stream().filter(x -> x.getType() == ZoneType.OrderZone).count();
        return (numOfOrderZones == 1);
    }

    public static boolean checkAllZonesInisideOrderZone(Set<Zone> zones){
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
    public static boolean checkAllZonesAreConnected(Set<Zone> zones){

        List<Polygon> polygonList = zones.stream()
                .filter(x -> x.getType() != ZoneType.OrderZone)
                .map(x -> convertZoneToJtsPolygon(x))
                .collect(Collectors.toList());

        Geometry unionOfPolygons = CascadedPolygonUnion.union(polygonList);

        return unionOfPolygons.getGeometryType().equals("Polygon");
    }

    public static boolean checkThatDroneIsInLoadingZone(Set<Zone> zones, org.geolatte.geom.Point dronePoint){
        Zone loadingZone = zones.stream().filter(x -> x.getType() == ZoneType.LoadingZone).findFirst().get();

        return JTS.to(loadingZone.getPolygon()).contains(JTS.to(dronePoint));
    }

    public static boolean isCustomerInsideDeliveryZone(Set<Zone> zones, org.geolatte.geom.Point customerPoint){
        int numberOfDeliveryZonesThatContainCustomer =
                (int) zones.stream()
                        .filter(x -> x.getType() == ZoneType.DeliveryZone)
                        .map(x -> JTS.to(x.getPolygon()).contains(JTS.to(customerPoint)))
                        .filter(x -> x == Boolean.TRUE)
                        .count();

        return numberOfDeliveryZonesThatContainCustomer > 0;
    }

    public static Polygon convertZoneToJtsPolygon(Zone x) {
        return (Polygon) JTS.to(x.getPolygon());
    }

}
