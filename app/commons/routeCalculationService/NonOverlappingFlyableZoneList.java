package service.routeCalculationService;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import service.gis.GisHelper;
import service.gis.RouteHelper;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.*;
import org.geolatte.geom.jts.JTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class NonOverlappingFlyableZoneList {

    private static final Logger logger = LoggerFactory.getLogger(NonOverlappingFlyableZoneList.class);

    private final List<NonOverlappingZone> zoneList;

    public NonOverlappingFlyableZoneList(Set<Zone> zones) {
        zoneList = zones.stream()
            .filter(x -> x.getType() != ZoneType.OrderZone)
            .map(NonOverlappingZone::new)
            .sorted(Comparator.comparing(NonOverlappingZone::getHeight))
            .collect(Collectors.toList());

        logger.debug("NonOverlappingFlyableZoneList Size {}", zoneList.size());
        removeOverlappingParts();
    }

    private void removeOverlappingParts(){
        Iterator<NonOverlappingZone> zoneIterator = zoneList.iterator();

        while(zoneIterator.hasNext()){
            NonOverlappingZone currentZone = zoneIterator.next();

            List<com.vividsolutions.jts.geom.Polygon> polygonsWithoutCurrentPolygon = zoneList.stream()
                .filter(x -> !x.equals(currentZone))
                .map(this::convertZoneToPolygon)
                .collect(Collectors.toList());

            Geometry unifiedPolygons = CascadedPolygonUnion.union(polygonsWithoutCurrentPolygon);

            Geometry difference = (JTS.to(currentZone.getPolygon())).difference(unifiedPolygons);
            logger.debug("Difference of Polygons {}", difference.toString());
            logger.debug("Type of Polygon is {}", difference.getGeometryType());

            /**
             * Is the case, when there were two identical polygon.
             * So the difference between current and ( rest polygon ) is empty.
             */
            boolean thereWasTwoSamePolygon = difference.isEmpty();
            if(thereWasTwoSamePolygon){
                zoneIterator.remove();
                return;
            }

            boolean noZoneSplit = difference.getGeometryType().equals("Polygon");
            if(noZoneSplit){
                Polygon subtractedZonePolygon = (Polygon) difference;
                currentZone.setPolygon((org.geolatte.geom.Polygon) JTS.from(subtractedZonePolygon, GisHelper.getReferenceSystem()));
                return;
            }

            /**
             * Is the case, when there current zone was split by the other zones
             */
            boolean currentZoneSplit = difference.getGeometryType().equals("MultiPolygon");
            if(currentZoneSplit){
                throw new RuntimeException("Case MULTIPOLYGON needs to be implemented!");
            }
        }
    }

    private com.vividsolutions.jts.geom.Polygon convertZoneToPolygon(NonOverlappingZone zone) {
        return (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());
    }

    public void logAllZones() {
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

        /**
         * For each segment of the line String
         */
        for (int i = 0; i < coordinates.length - 1; i++) {
            GeometryFactory geometryFactory = new GeometryFactory();


            Coordinate startOfSegment = coordinates[i];
            Coordinate endOfSegment = coordinates[i + 1];

            com.vividsolutions.jts.geom.LineString lineStringSegment =
                geometryFactory.createLineString(new Coordinate[]{startOfSegment, endOfSegment});

            for (NonOverlappingZone zone : zoneList) {

                com.vividsolutions.jts.geom.Polygon currentPolygon = (com.vividsolutions.jts.geom.Polygon) JTS.to(zone.getPolygon());
                com.vividsolutions.jts.geom.LineString exteriorRing = currentPolygon.getExteriorRing();

                boolean segmentIntersectsWithPolygonBorder = exteriorRing.intersects(lineStringSegment);
                if(segmentIntersectsWithPolygonBorder){

                    Geometry intersections = exteriorRing.intersection(lineStringSegment);
                    logger.debug("Intersection object is {}", intersections.toString());

                    Coordinate[] intersectionPoints = intersections.getCoordinates();

                    for (Coordinate coordinate : intersectionPoints) {
                        returnLineStringCoordinates.add(coordinate);
                        logger.debug("Intersection Coordinate is {}", coordinate.toString());
                    }
                }
            }

            /**
             *  Because we want a LineSting finally
             */
            returnLineStringCoordinates.add(endOfSegment);
        }

        return RouteHelper.coordinatesToLineString(returnLineStringCoordinates);
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