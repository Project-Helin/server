package commons.routeCalculationService;

import ch.helin.commons.AssertUtils;
import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import commons.gis.GisHelper;
import commons.gis.RouteHelper;
import commons.gis.ZoneHelper;
import dao.ProjectsDao;
import dao.RouteDao;
import models.Project;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.*;
import org.geolatte.geom.jts.JTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RouteCalculationService {

    @Inject
    private RouteDao routeDao;

    @Inject
    private ProjectsDao projectsDao;

    private static final Logger logger = LoggerFactory.getLogger(RouteCalculationService.class);

    public List<ch.helin.messages.dto.way.Position> calculateRoute(ch.helin.messages.dto.way.Position customerPosition,
                                                                   Project project) {
        Point pointOnPolygon = projectsDao.findPointOnLoadingZone(project.getId());
        AssertUtils.throwExceptionIfNull(pointOnPolygon, "Point on polygon could not be calculated.");

        return calculateRoute(GisHelper.createPosition(pointOnPolygon), customerPosition, project);
    }

    public List<ch.helin.messages.dto.way.Position> calculateRoute(ch.helin.messages.dto.way.Position dronePosition,
                                                                   ch.helin.messages.dto.way.Position customerPosition,
                                                                   Project project) {

        logger.info("State of calculateRoute dronePosition {}", AssertUtils.throwExceptionIfNull(dronePosition));
        logger.info("State of calculateRoute customerPosition {}", AssertUtils.throwExceptionIfNull(customerPosition));
        logger.info("State of calculateRoute Project {}", AssertUtils.throwExceptionIfNull(project));

        MultiLineString skeletonMultiLine = routeDao.calculateSkeleton(project.getId());
        logger.debug("Calculated skeleton {}", GisHelper.toWktStringWithoutSrid(skeletonMultiLine));

        org.geolatte.geom.Point dronePoint = GisHelper.createPoint(dronePosition.getLon(), dronePosition.getLat());

        RouteHelper routeHelper = new RouteHelper();
        LineString lineStringToDrone = routeHelper.calculateShortestLineToPoint(skeletonMultiLine, dronePoint);
        logger.debug("Drone-to-Skeleton: {}", GisHelper.toWktStringWithoutSrid(lineStringToDrone));

        RawGraph rawGraph = new RawGraph();
        rawGraph.addLineString(lineStringToDrone);
        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToDrone);
        logger.debug("Skeleton after split: {}", skeletonMultiLine);

        org.geolatte.geom.Point customerPoint = GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat());

        Point realDropPoint = customerPoint;
        if(!ZoneHelper.isCustomerInsideDeliveryZone(project.getZones(), customerPoint)){
            realDropPoint = calculateDroPoint(project, customerPoint);
        }

        LineString lineStringToCustomer = routeHelper.calculateShortestLineToPoint(skeletonMultiLine, realDropPoint);
        rawGraph.addLineString(lineStringToCustomer);

        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToCustomer);
        rawGraph.addMultiLineString(skeletonMultiLine);

        Dijkstra dijkstra = new Dijkstra(rawGraph);
        List<Position> shortestPath = dijkstra.calculateShortestPath(
            lineStringToDrone.getEndPosition(), lineStringToCustomer.getEndPosition());

        LineString lineStringFromPositions = positionListtoLineString(shortestPath);

        return calculateHeightForFlightPath(project.getZones(), lineStringFromPositions);
    }

    private Point calculateDroPoint(Project project, Point customerPoint) {
        Point realDropPoint;
        logger.debug("Customer is not in DeliveryZone.");

        List<com.vividsolutions.jts.geom.Polygon> polygonList = project.
                getZones().stream()
                .filter(x -> x.getType() == ZoneType.DeliveryZone)
                .map(ZoneHelper::convertZoneToJtsPolygon)
                .collect(Collectors.toList());

        GeometryFactory polygonFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.MultiPolygon deliveryZonePolygons =
            polygonFactory.createMultiPolygon(polygonList.toArray(new com.vividsolutions.jts.geom.Polygon[]{}));

        org.geolatte.geom.MultiPolygon zoneMultiPolygon = (org.geolatte.geom.MultiPolygon) JTS.from(deliveryZonePolygons, GisHelper.getReferenceSystem());

        realDropPoint = getIntersectionPointWithPolygon(zoneMultiPolygon, customerPoint);
        return realDropPoint;
    }

    private List<ch.helin.messages.dto.way.Position> calculateHeightForFlightPath(Set<Zone> zones,
                                                                                  LineString lineString) {

        NonOverlappingFlyableZoneList nonOverlappingZoneList = new NonOverlappingFlyableZoneList(zones);
        nonOverlappingZoneList.debugZoneList();
        LineString lineString1 = nonOverlappingZoneList.cutLineStringOnPolygonBorder(lineString);

        List<Position> positionList = new ArrayList<>();

        PositionSequence positionSeq = lineString1.getPositions();
        for(int i = 0; i<positionSeq.size(); i++){
            positionList.add(positionSeq.getPositionN(i));
        }

        List<ch.helin.messages.dto.way.Position> positions = nonOverlappingZoneList.assignHeightForPositions(positionList);

        logger.debug("List waypoints with Height {}", positions.toString());

        return positions;
    }

    private org.geolatte.geom.Point getIntersectionPointWithPolygon(org.geolatte.geom.MultiPolygon deliveryZonePolygon, org.geolatte.geom.Point customerPoint) {
        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(customerPoint);
        com.vividsolutions.jts.geom.MultiPolygon jtsPolygon = (com.vividsolutions.jts.geom.MultiPolygon) JTS.to(deliveryZonePolygon);

        Coordinate[] coordinates = DistanceOp.nearestPoints(jtsPolygon, jtsPoint);

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.Point intersectionPoint = geometryFactory.createPoint(coordinates[0]);

        return (org.geolatte.geom.Point) JTS.from(intersectionPoint, GisHelper.getReferenceSystem());
    }

    public MultiLineString splitMultiLineStringOnPoint(MultiLineString skeleton, Point point){

        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(point);
        com.vividsolutions.jts.geom.MultiLineString jtsMultiLine = (com.vividsolutions.jts.geom.MultiLineString) JTS.to(skeleton);

        LinkedList<com.vividsolutions.jts.geom.LineString> lineStrings = new LinkedList<>();
        for(int i=0; i< jtsMultiLine.getNumGeometries(); i++){

            com.vividsolutions.jts.geom.LineString currentLineSting =
                    (com.vividsolutions.jts.geom.LineString) jtsMultiLine.getGeometryN(i);

            if(jtsPoint.isWithinDistance(currentLineSting, GisHelper.getRoundoffPrecision()) && !JtsObjectManipulator.isPointOnLineVertex(jtsPoint, jtsMultiLine)){
                System.out.println("SPLIT ON: " + GisHelper.toWktStringWithoutSrid(point));
                // Split current line if point is on and not on point
                logger.info("SPLIT ON: {} ", GisHelper.toWktStringWithoutSrid(point));
                com.vividsolutions.jts.geom.Point start = currentLineSting.getStartPoint();
                com.vividsolutions.jts.geom.Point end = currentLineSting.getEndPoint();

                lineStrings.add(JtsObjectManipulator.pointsToLineString(start, jtsPoint));
                lineStrings.add(JtsObjectManipulator.pointsToLineString(jtsPoint, end));

            } else{
                lineStrings.add(currentLineSting);
            }
        }

        com.vividsolutions.jts.geom.LineString[] lineStringArray = lineStrings.toArray(new com.vividsolutions.jts.geom.LineString[]{});

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.MultiLineString returnMultiLineString = geometryFactory.createMultiLineString(lineStringArray);

        return (MultiLineString) JTS.from(returnMultiLineString, skeleton.getCoordinateReferenceSystem());

    }

    private MultiLineString splitMultiLineStringOnLineString(MultiLineString  skeleton, LineString lineString){
        Position startPosition = lineString.getStartPosition();
        Position endPosition = lineString.getEndPosition();

        Point startPoint = GisHelper.createPoint(startPosition.getCoordinate(0), startPosition.getCoordinate(1));
        Point endPoint = GisHelper.createPoint(endPosition.getCoordinate(0), endPosition.getCoordinate(1));

        // we need to run the split function twice, because
        // we don't know if startPoint or endPoint is on the skeleton
        skeleton = splitMultiLineStringOnPoint(skeleton, startPoint);
        return splitMultiLineStringOnPoint(skeleton, endPoint);
    }


    public LineString positionListtoLineString(List<Position> positionList){
        List<Coordinate> coordinateList = new ArrayList<>();

        for (org.geolatte.geom.Position position : positionList) {
            double lon = position.getCoordinate(0); // <<-- this 0 sucks, but is the x component
            double lat = position.getCoordinate(1);

            coordinateList.add(new Coordinate(lon, lat));
        }

        GeometryFactory gf = new GeometryFactory();
        com.vividsolutions.jts.geom.LineString lineString = gf.createLineString(coordinateList.toArray(new Coordinate[]{}));
        return (LineString) JTS.from(lineString, GisHelper.getReferenceSystem());
    }

}

