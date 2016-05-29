package commons.routeCalculationService;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import commons.gis.GisHelper;
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
            NonOverlappingZone zone = zoneIterator.next();

            List<com.vividsolutions.jts.geom.Polygon> collect = zoneList.stream()
                .filter(x -> !x.equals(zone))
                .map(this::convertZoneToPolygon)
                .collect(Collectors.toList());

            Geometry unifiedPolygons = CascadedPolygonUnion.union(collect);

            Geometry difference = (JTS.to(zone.getPolygon())).difference(unifiedPolygons);
            logger.debug("Difference of Polygons {}", difference.toString());
            logger.debug("Type of Polygon is {}", difference.getGeometryType());

            if(difference.isEmpty()){
                zoneIterator.remove();
                return;
            }

            if(difference.getGeometryType().equals("Polygon")){
                Polygon subtractedZonePolygon = (Polygon) difference;
                zone.setPolygon((org.geolatte.geom.Polygon) JTS.from(subtractedZonePolygon, GisHelper.getReferenceSystem()));
                return;
            }

            if(difference.getGeometryType().equals("MultiPolygon")){
                throw new RuntimeException("Case MULTIPOLYGON needs to be implemented!");
            }
        }
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
                    Geometry intersection = exteriorRing.intersection(lineStringSegment);
                    logger.debug("Intersection object is {}", intersection.toString());

                    Coordinate[] coordinateArray = intersection.getCoordinates();

                    for (Coordinate coordinate : coordinateArray) {
                        returnLineStringCoordinates.add(coordinate);
                        logger.debug("Intersection Coordinate is {}", coordinate.toString());
                    }
                }
            }
            returnLineStringCoordinates.add(coordinates[i+1]);
        }

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