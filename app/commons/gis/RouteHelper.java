package service.gis;

import ch.helin.messages.dto.way.Position;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.MultiPolygon;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.jts.JTS;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RouteHelper {

    public static org.geolatte.geom.LineString calculateShortestLineToPoint(org.geolatte.geom.Geometry geometryObject,
                                                                            org.geolatte.geom.Point point) {

        Point jtsPoint = (Point) JTS.to(point);
        Geometry jtsGeometry = JTS.to(geometryObject);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsGeometry, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString jtsResultLineString = geometryFactory.createLineString(coordinates);

        return (org.geolatte.geom.LineString) JTS.from(jtsResultLineString, GisHelper.getReferenceSystem());
    }

    public static org.geolatte.geom.LineString calculateShortestLineToPoint(org.geolatte.geom.Geometry geometryObject,
                                                                            Position point) {

        org.geolatte.geom.Point geomPoint = GisHelper.createPoint(point.getLon(), point.getLat());
        return calculateShortestLineToPoint(geometryObject, geomPoint);
    }

    public static MultiPolygon polygonListToMultiPolygon(List<Polygon> onlyDeliveryZones) {
        GeometryFactory polygonFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.MultiPolygon deliveryZonePolygons =
            polygonFactory.createMultiPolygon(onlyDeliveryZones.toArray(new com.vividsolutions.jts.geom.Polygon[]{}));

        org.geolatte.geom.MultiPolygon zoneMultiPolygon =
            (org.geolatte.geom.MultiPolygon) JTS.from(deliveryZonePolygons, GisHelper.getReferenceSystem());
        return zoneMultiPolygon;
    }

    public static org.geolatte.geom.Point getIntersectionPointWithPolygon(org.geolatte.geom.MultiPolygon deliveryZonePolygon,
                                                                          org.geolatte.geom.Point customerPoint) {

        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(customerPoint);
        com.vividsolutions.jts.geom.MultiPolygon jtsPolygon = (com.vividsolutions.jts.geom.MultiPolygon) JTS.to(deliveryZonePolygon);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsPolygon, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.Point intersectionPoint = geometryFactory.createPoint(coordinates[0]);

        return (org.geolatte.geom.Point) JTS.from(intersectionPoint, GisHelper.getReferenceSystem());
    }

    public static org.geolatte.geom.LineString positionListToLineString(List<org.geolatte.geom.Position > positionList){
        List<Coordinate> coordinateList = new ArrayList<>(positionList.size());

        for (org.geolatte.geom.Position position : positionList) {
            double lon = position.getCoordinate(0); // <<-- this 0 sucks, but is the x component
            double lat = position.getCoordinate(1);

            coordinateList.add(new Coordinate(lon, lat));
        }

        GeometryFactory gf = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString lineString = gf.createLineString(coordinateList.toArray(new Coordinate[]{}));
        return (org.geolatte.geom.LineString) JTS.from(lineString, GisHelper.getReferenceSystem());
    }

    public static MultiLineString lineListToMultiLine(LinkedList<LineString> lineStrings,
                                                      CoordinateReferenceSystem coordinateReferenceSystem) {

        com.vividsolutions.jts.geom.LineString[] lineStringArray =
            lineStrings.toArray(new com.vividsolutions.jts.geom.LineString[]{});

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.MultiLineString returnMultiLineString =
            geometryFactory.createMultiLineString(lineStringArray);

        return (MultiLineString) JTS.from(returnMultiLineString, coordinateReferenceSystem);
    }

    public static org.geolatte.geom.LineString coordinatesToLineString(List<Coordinate> returnLineStringCoordinates) {
        GeometryFactory returnLineStringFactory = new GeometryFactory();
        Coordinate[] returnCoordinates = returnLineStringCoordinates.toArray(new Coordinate[]{});
        com.vividsolutions.jts.geom.LineString returnLineString = returnLineStringFactory.createLineString(returnCoordinates);

        return (org.geolatte.geom.LineString) JTS.from(returnLineString, GisHelper.getReferenceSystem());
    }
}
