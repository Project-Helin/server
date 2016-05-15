package commons.routeCalculationService;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import commons.gis.GisHelper;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.LineString;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.jts.JTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UnoverlappingFlyableZoneList {

    private static final Logger logger = LoggerFactory.getLogger(UnoverlappingFlyableZoneList.class);

    private List<UnoverlappingZone> zoneList;

    public UnoverlappingFlyableZoneList(Set<Zone> zones) {
        zoneList = zones.stream().filter(x -> x.getType() != ZoneType.OrderZone).map(UnoverlappingZone::new).sorted(Comparator.comparing(x -> x.getHeight())).collect(Collectors.toList());
        logger.debug("UnoverlappingFlyableZoneList Size {}", zoneList.size());
        removeOverlappingParts();
    }

    private void removeOverlappingParts(){
        for (UnoverlappingZone zone : zoneList) {
            List<com.vividsolutions.jts.geom.Polygon> collect = zoneList.stream().filter(x -> !x.equals(zone)).map(x -> convertZoneToPolygon(x)).collect(Collectors.toList());
            Geometry unifiedPolygons = CascadedPolygonUnion.union(collect);

            com.vividsolutions.jts.geom.Polygon subtractedZonePolygon = (com.vividsolutions.jts.geom.Polygon) (JTS.to(zone.getPolygon())).difference(unifiedPolygons);
            zone.setPolygon((Polygon) JTS.from(subtractedZonePolygon, GisHelper.getReferenceSystem()));
        }
    }

    public List<UnoverlappingZone> getZoneList() {
        return zoneList;
    }

    public void setZoneList(ArrayList<UnoverlappingZone> zoneList) {
        this.zoneList = zoneList;
    }

    private com.vividsolutions.jts.geom.Polygon convertZoneToPolygon(UnoverlappingZone zone) {
        return (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());
    }

    public void debugZoneList() {
        for (UnoverlappingZone zone : zoneList) {
            logger.info("ZoneList Debug {}", zone.getPolygon().toString());
        }
    }

    public LineString cutLineStringOnPolygonBorder(LineString lineString){
        return null;
    }
}