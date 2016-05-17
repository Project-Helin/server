package commons.routeCalculationService;


import ch.helin.messages.dto.Action;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import commons.gis.GisHelper;
import models.WayPoint;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.Position;
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
            zone.setPolygon((org.geolatte.geom.Polygon) JTS.from(subtractedZonePolygon, GisHelper.getReferenceSystem()));
        }
    }

    public List<UnoverlappingZone> getZoneList() {
        return zoneList;
    }

    private com.vividsolutions.jts.geom.Polygon convertZoneToPolygon(UnoverlappingZone zone) {
        return (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());
    }

    public void debugZoneList() {
        for (UnoverlappingZone zone : zoneList) {
            logger.info("ZoneList Debug {}", zone.getPolygon().toString());
        }
    }

    public org.geolatte.geom.LineString cutLineStringOnPolygonBorder(org.geolatte.geom.LineString lineString){
        logger.debug("Input to cutLineStringOnPolygonBorder geometry {}", lineString.toString());
        com.vividsolutions.jts.geom.LineString jtsLineString = (com.vividsolutions.jts.geom.LineString) JTS.to(lineString);

        Coordinate[] coordinates = jtsLineString.getCoordinates();

        List<Coordinate> returnLineStringCoordinates = new ArrayList<>();
        returnLineStringCoordinates.add(coordinates[0]); //add this point, because it is never part of a line...

        for(int i=0; i<coordinates.length-1; i++){
            GeometryFactory geometryFactory = new GeometryFactory();
            com.vividsolutions.jts.geom.LineString lineStringSegment = geometryFactory.createLineString(new Coordinate[]{coordinates[i], coordinates[i + 1]});

            for (UnoverlappingZone zone : zoneList) {
                com.vividsolutions.jts.geom.Polygon currentPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());
                com.vividsolutions.jts.geom.LineString exteriorRing = currentPolygon.getExteriorRing();
                if(exteriorRing.intersects(lineStringSegment)){
                    logger.debug("FUCK YEAH! INTERSECTION!");
                    Point intersectionPoint = (Point) exteriorRing.intersection(lineStringSegment);

                    returnLineStringCoordinates.add(intersectionPoint.getCoordinate());
                    logger.debug("Intersection Coordinate is {}", intersectionPoint.getCoordinate());
                }
            }
            returnLineStringCoordinates.add(coordinates[i+1]);
        }

        List<com.vividsolutions.jts.geom.Polygon> collect = zoneList.stream().map(x -> convertZoneToPolygon(x)).collect(Collectors.toList());

        GeometryFactory returnLineStringFactory = new GeometryFactory();
        Coordinate[] returnCoordinates = returnLineStringCoordinates.toArray(new Coordinate[]{});
        com.vividsolutions.jts.geom.LineString returnLineString = returnLineStringFactory.createLineString(returnCoordinates);

        logger.debug("Output to cutLineStringOnPolygonBorder geometry {}", returnLineString.toString());
        //com.vividsolutions.jts.geom.LineString intersectionLine = (com.vividsolutions.jts.geom.LineString) jtsLineString.intersection(multiPolygonOfUnoverlappedZones);

        return (org.geolatte.geom.LineString) JTS.from(returnLineString, GisHelper.getReferenceSystem());
    }

    public List<WayPoint> assignHeightForPositions(List<Position> positionList) {

        List<WayPoint> listWayPoints = new ArrayList<>();

        for (Position position : positionList) {

            org.geolatte.geom.Point point = GisHelper.createPoint(position.getCoordinate(0), position.getCoordinate(1));
            Point jtsPoint = (Point) JTS.to(point);

            for (UnoverlappingZone zone : zoneList) {
                com.vividsolutions.jts.geom.Polygon currentPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());

                if(currentPolygon.contains(jtsPoint)){
                    WayPoint wp = new WayPoint();
                    wp.setHeight(zone.getHeight());
                    wp.setPosition(point);
                    wp.setAction(Action.FLY);
                    wp.setOrderNumber(listWayPoints.size());

                    listWayPoints.add(wp);
                }
            }
        }

        return listWayPoints;

    }
}