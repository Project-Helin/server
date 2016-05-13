package dao;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.TestHelper;
import commons.gis.GisHelper;
import models.Project;
import models.ZoneType;
import org.geolatte.geom.Point;
import org.junit.Test;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class ProjectsDaoTest extends AbstractIntegrationTest {

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private TestHelper testHelper;

    @Inject
    private JPAApi jpaApi;

    @Test
    @Transactional
    public void findPointOnLoadingZone() {
        jpaApi.withTransaction(() -> {
            Project project = testHelper.createNewProject(
                testHelper.createNewOrganisation(),
                testHelper.createUnsavedZone(
                    "My test zone",
                    ZoneType.LoadingZone,

                    // Polygon is a square
                    GisHelper.convertFromWktToGeometry("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))")
                )
            );

            Point middlePoint = projectsDao.findPointOnLoadingZone(project.getId());

            assertThat(GisHelper.toWktStringWithoutSrid(middlePoint)).isEqualTo("POINT(2.5 2.5)");
        });
    }
}