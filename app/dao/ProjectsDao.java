package dao;

import commons.gis.GisHelper;
import models.Organisation;
import models.Project;
import org.geolatte.geom.*;
import org.slf4j.Logger;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class ProjectsDao extends AbstractDao<Project> {
    private static final Logger logger = getLogger(ProjectsDao.class);

    public ProjectsDao() {
        super(Project.class, "projects");
    }

    public List<Project> findByOrganisation(UUID organisationId) {
        return jpaApi.em()
                .createQuery(
                        "select e from projects e where e.organisation.id = :organisation ",
                        Project.class
                )
                .setParameter("organisation", organisationId)
                .getResultList();
    }

    public Project findByIdAndOrganisation(UUID projectId, Organisation organisation) {
        TypedQuery<Project> query = jpaApi
            .em()
            .createQuery(
                "select p from projects p " +
                " where p.organisation = :organisation " +
                " and p.id = :projectId",
                Project.class
            )
            .setParameter("projectId", projectId)
            .setParameter("organisation", organisation);

        return getSingleResultOrNull(query);
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
