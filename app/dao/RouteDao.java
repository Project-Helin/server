package dao;

import commons.gis.GisHelper;
import models.Route;
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

    public RouteDao() {
        super(Route.class, "routes");
    }

    public MultiLineString calculateSkeleton(UUID projectId){
        Query nativeQuery = jpaApi.em().createNativeQuery(
                "SELECT ST_asText(ST_ApproximateMedialAxis(ST_UNION(polygon\\:\\:geometry))) " +
                        " FROM zones " +
                        " WHERE project_id = :projectId AND type != 'OrderZone'"
        );
        nativeQuery.setParameter("projectId", projectId);


        String wktString = (String) nativeQuery.getSingleResult();
        logger.info("singleResult=[{}]", wktString);

        MultiLineString multiLineString = GisHelper.convertFromWktToGeometry(wktString);

        return multiLineString;
    }

}
