package service.routeCalculationService;

import ch.helin.commons.AssertUtils;
import com.google.inject.Inject;
import service.gis.GisHelper;
import service.gis.RouteHelper;
import service.gis.ZoneHelper;
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

/**
 * The mapping between our Position, JTS and Geolatte is needed because,
 * JTS is Java Topology Suite is used to function on geometries.
 * <p>
 * Geolatte is used by Hibernate Spatial to persist in the database and does not provide
 * all the needed functions. That's the reason why we need to map from JTS to Geolatte and back
 */
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

        LineString lineStringToDrone = RouteHelper.calculateShortestLineToPoint(skeletonMultiLine, dronePosition);
        logger.debug("Drone-to-Skeleton: {}", GisHelper.toWktStringWithoutSrid(lineStringToDrone));

        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToDrone);
        logger.debug("Skeleton after split: {}", skeletonMultiLine);

        org.geolatte.geom.Point customerPoint =
            GisHelper.createPoint(customerPosition.getLon(), customerPosition.getLat());

        Point realDropPoint = customerPoint;
        if (!ZoneHelper.isCustomerInsideDeliveryZone(project.getZones(), customerPoint)) {
            realDropPoint = calculateDropPoint(project, customerPoint);
        }

        LineString lineStringToCustomer = RouteHelper.calculateShortestLineToPoint(skeletonMultiLine, realDropPoint);
        skeletonMultiLine = splitMultiLineStringOnLineString(skeletonMultiLine, lineStringToCustomer);

        RawGraph rawGraph = new RawGraph();
        rawGraph.addLineString(lineStringToDrone);
        rawGraph.addLineString(lineStringToCustomer);
        rawGraph.addMultiLineString(skeletonMultiLine);
        Dijkstra dijkstra = new Dijkstra(rawGraph);

        List<Position> shortestPath = dijkstra.calculateShortestPath(
            lineStringToDrone.getEndPosition(),
            lineStringToCustomer.getEndPosition()
        );

        LineString lineStringFromPositions = RouteHelper.positionListToLineString(shortestPath);
        return calculateHeightForFlightPath(project.getZones(), lineStringFromPositions);
    }

    private Point calculateDropPoint(Project project, Point customerPoint) {
        logger.debug("Customer is not in DeliveryZone.");

        List<com.vividsolutions.jts.geom.Polygon> onlyDeliveryZones = project.
            getZones()
            .stream()
            .filter(x -> x.getType() == ZoneType.DeliveryZone)
            .map(ZoneHelper::convertZoneToJtsPolygon)
            .collect(Collectors.toList());

        org.geolatte.geom.MultiPolygon zoneMultiPolygon = RouteHelper.polygonListToMultiPolygon(onlyDeliveryZones);

        Point realDropPoint = RouteHelper.getIntersectionPointWithPolygon(zoneMultiPolygon, customerPoint);
        return realDropPoint;
    }

    private List<ch.helin.messages.dto.way.Position> calculateHeightForFlightPath(Set<Zone> zones,
                                                                                  LineString lineString) {

        NonOverlappingFlyableZoneList nonOverlappingZoneList = new NonOverlappingFlyableZoneList(zones);
        nonOverlappingZoneList.logAllZones();

        LineString lineStringToBorder = nonOverlappingZoneList.cutLineStringOnPolygonBorder(lineString);
        List<Position> positionList = convertToPositionList(lineStringToBorder);

        List<ch.helin.messages.dto.way.Position> positions =
            nonOverlappingZoneList.assignHeightForPositions(positionList);

        logger.debug("List waypoints with Height {}", positions.toString());
        return positions;
    }

    private List<Position> convertToPositionList(LineString lineStringToBorder) {
        PositionSequence positionSeq = lineStringToBorder.getPositions();

        List<Position> positionList = new ArrayList<>();
        for (int i = 0; i < positionSeq.size(); i++) {
            positionList.add(positionSeq.getPositionN(i));
        }
        return positionList;
    }

    public MultiLineString splitMultiLineStringOnPoint(MultiLineString skeleton, Point point) {

        com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) JTS.to(point);
        com.vividsolutions.jts.geom.MultiLineString jtsMultiLine = (com.vividsolutions.jts.geom.MultiLineString) JTS.to(skeleton);

        LinkedList<com.vividsolutions.jts.geom.LineString> lineStrings = new LinkedList<>();
        for (int i = 0; i < jtsMultiLine.getNumGeometries(); i++) {

            com.vividsolutions.jts.geom.LineString currentLineSting =
                (com.vividsolutions.jts.geom.LineString) jtsMultiLine.getGeometryN(i);

            boolean isOnLine = jtsPoint.isWithinDistance(currentLineSting, GisHelper.getRoundoffPrecision());
            boolean isOnLineVertex = !JtsObjectManipulator.isPointOnLineVertex(jtsPoint, jtsMultiLine);

            boolean weNeedToSplitLine = isOnLine && isOnLineVertex;
            if (weNeedToSplitLine) {
                // Split current line if point is on and not on point
                logger.info("SPLIT ON: {} ", GisHelper.toWktStringWithoutSrid(point));
                com.vividsolutions.jts.geom.Point start = currentLineSting.getStartPoint();
                com.vividsolutions.jts.geom.Point end = currentLineSting.getEndPoint();

                lineStrings.add(JtsObjectManipulator.pointsToLineString(start, jtsPoint));
                lineStrings.add(JtsObjectManipulator.pointsToLineString(jtsPoint, end));
            } else {
                lineStrings.add(currentLineSting);
            }
        }
        return RouteHelper.lineListToMultiLine(lineStrings, skeleton.getCoordinateReferenceSystem());
    }

    private MultiLineString splitMultiLineStringOnLineString(MultiLineString skeleton, LineString lineString) {
        Position startPosition = lineString.getStartPosition();
        Position endPosition = lineString.getEndPosition();

        Point startPoint = GisHelper.createPoint(startPosition.getCoordinate(0), startPosition.getCoordinate(1));
        Point endPoint = GisHelper.createPoint(endPosition.getCoordinate(0), endPosition.getCoordinate(1));

        // we need to run the split function twice, because
        // we don't know if startPoint or endPoint is on the skeleton
        skeleton = splitMultiLineStringOnPoint(skeleton, startPoint);
        return splitMultiLineStringOnPoint(skeleton, endPoint);
    }


}

