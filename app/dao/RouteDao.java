package dao;

import ch.helin.messages.dto.way.Route;
import commons.gis.GisHelper;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Point;
import org.geolatte.geom.Position;
import org.slf4j.Logger;

import javax.persistence.Query;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;


public class RouteDao extends AbstractDao<Route>{
    private static final Logger logger = getLogger(RouteDao.class);

    public RouteDao(Class<Route> entityClass, String tableName) {
        super(entityClass, tableName);
    }

    public List<LineString> calculateSkeleton(UUID projectId){
        Query nativeQuery = jpaApi.em().createNativeQuery(
                "SELECT ST_asText((ST_Dump((ST_ApproximateMedialAxis(ST_UNION(polygon\\:\\:geometry))))).geom) " +
                        " FROM zones " +
                        " WHERE project_id = :projectId AND type != 'OrderZone'"
        );
        nativeQuery.setParameter("projectId", projectId);


        List<String> wktList = nativeQuery.getResultList();
        logger.info("singleResult=[{}]", wktList);

        List<LineString> lineStringList = wktList
                .stream()
                .map((a) -> (LineString) GisHelper.convertFromWktToGeometry(a))
                .collect(Collectors.toList());

        return lineStringList;
    }

    public LineString calculateShortestLineToPoint(MultiLineString<Position> lineStrings, Point point) {
        Query nativeQuery = jpaApi.em().createNativeQuery(
                "SELECT ST_asText(ST_ShortestLine(:lineStrings, :objPosition))"
        );
        nativeQuery.setParameter("lineStrings", lineStrings);
        nativeQuery.setParameter("objPosition", point);

        String resultString = (String) nativeQuery.getSingleResult();
        LineString resultLineString = (LineString) GisHelper.convertFromWktToGeometry(resultString);

        logger.info("calculateShortestLineToPoint=[{}]", resultLineString);

        return resultLineString;

    }


}
