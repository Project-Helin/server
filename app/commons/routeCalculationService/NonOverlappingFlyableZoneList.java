package commons.routeCalculationService;


import ch.helin.messages.dto.Action;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import commons.gis.GisHelper;
import models.Route;
import models.WayPoint;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.Position;
import org.geolatte.geom.jts.JTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class NonOverlappingFlyableZoneList {

    private static final Logger logger = LoggerFactory.getLogger(NonOverlappingFlyableZoneList.class);

    private List<NonOverlappingZone> zoneList;

    public NonOverlappingFlyableZoneList(Set<Zone> zones) {
        zoneList = zones.stream().filter(x -> x.getType() != ZoneType.OrderZone).map(NonOverlappingZone::new).sorted(Comparator.comparing(x -> x.getHeight())).collect(Collectors.toList());
        logger.debug("NonOverlappingFlyableZoneList Size {}", zoneList.size());
        removeOverlappingParts();
    }

    private void removeOverlappingParts(){
        Iterator<NonOverlappingZone> zoneIterator = zoneList.iterator();

        while(zoneIterator.hasNext()){
            NonOverlappingZone zone = zoneIterator.next();

            List<com.vividsolutions.jts.geom.Polygon> collect = zoneList.stream().filter(x -> !x.equals(zone)).map(x -> convertZoneToPolygon(x)).collect(Collectors.toList());
            Geometry unifiedPolygons = CascadedPolygonUnion.union(collect);


            com.vividsolutions.jts.geom.Polygon subtractedZonePolygon = (com.vividsolutions.jts.geom.Polygon) (JTS.to(zone.getPolygon())).difference(unifiedPolygons);
            if(subtractedZonePolygon.isEmpty()){
                zoneIterator.remove();
                return;
            }
            zone.setPolygon((org.geolatte.geom.Polygon) JTS.from(subtractedZonePolygon, GisHelper.getReferenceSystem()));
        }
    }

    public List<NonOverlappingZone> getZoneList() {
        return zoneList;
    }

    private com.vividsolutions.jts.geom.Polygon convertZoneToPolygon(NonOverlappingZone zone) {
        return (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());
    }

    public void debugZoneList() {
        for (NonOverlappingZone zone : zoneList) {
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

            for (NonOverlappingZone zone : zoneList) {
                com.vividsolutions.jts.geom.Polygon currentPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());
                com.vividsolutions.jts.geom.LineString exteriorRing = currentPolygon.getExteriorRing();
                if(exteriorRing.intersects(lineStringSegment)){
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

        return (org.geolatte.geom.LineString) JTS.from(returnLineString, GisHelper.getReferenceSystem());
    }

    public List<ch.helin.messages.dto.way.Position> assignHeightForPositions(List<Position> positionList) {

        List<ch.helin.messages.dto.way.Position> positions = new ArrayList<>();

        for (Position geoLattePosition : positionList) {

            org.geolatte.geom.Point point = GisHelper.createPoint(geoLattePosition.getCoordinate(0), geoLattePosition.getCoordinate(1));
            Point jtsPoint = (Point) JTS.to(point);

            for (NonOverlappingZone zone : zoneList) {
                com.vividsolutions.jts.geom.Polygon currentPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());

                if(currentPolygon.contains(jtsPoint)){
                    ch.helin.messages.dto.way.Position wPoint = new ch.helin.messages.dto.way.Position();
                    wPoint.setLat(geoLattePosition.getCoordinate(1));
                    wPoint.setLon(geoLattePosition.getCoordinate(0));
                    wPoint.setHeight(zone.getHeight());
                    positions.add(wPoint);
                }
            }
        }

        return positions;

    }
}