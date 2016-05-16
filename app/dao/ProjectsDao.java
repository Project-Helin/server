package dao;

import commons.gis.GisHelper;
import models.Organisation;
import models.Project;
import models.ZoneType;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.slf4j.Logger;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

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

    public Point findPointOnLoadingZone(UUID projectId) {
        Query nativeQuery = jpaApi.em().createNativeQuery(
            "SELECT ST_asText(ST_PointOnSurface(polygon\\:\\:geometry))" +
            " FROM zones z " +
            " WHERE z.project_id = :projectId and z.type = :zoneType and z.polygon is not NULL "
        );
        nativeQuery.setParameter("projectId", projectId);
        nativeQuery.setParameter("zoneType", ZoneType.LoadingZone.name());

        List<String> resultList = nativeQuery.getResultList();

        if (resultList.size() == 1) {
            return GisHelper.convertFromWktToGeometry(resultList.get(0));
        }

        if (resultList.size() > 1) {
            logger.debug("Project: {}", projectId);
            throw new RuntimeException("More than one loading zone found");
        }else {
            logger.debug("Project: {}", projectId);
            throw new RuntimeException("No loading zone found with polygon defined.");
        }
    }
}
