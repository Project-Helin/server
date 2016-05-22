package dao;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.ImprovedTestHelper;
import commons.TestHelper;
import commons.gis.GisHelper;
import models.Organisation;
import models.Project;
import models.Zone;
import models.ZoneType;
import org.geolatte.geom.Point;
import org.junit.Test;
import org.omg.PortableInterceptor.AdapterStateHelper;
import org.slf4j.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fest.assertions.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class ProjectsDaoTest extends AbstractIntegrationTest {

    private static final Logger logger = getLogger(ProjectsDaoTest.class);

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private ImprovedTestHelper testHelper;

    @Inject
    private JPAApi jpaApi;

    @Test
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

    /**
     * This test is here to check if hibernate is corretly configured
     * to save alle zones with all projects.
     *
     * I had problem with that and wrote this test. Lets keep is as
     * a safety net.
     */
    @Test
    public void checkIfZoneArePersistedWithProject() {

        Project saved = jpaApi.withTransaction((em) -> {
            Project project = new Project();
            project.setName("First Demo");

            Zone zone = testHelper.createUnsavedZone("ja", ZoneType.DeliveryZone);
            zone.setProject(project);

            project.setZones(Stream.of(zone).collect(Collectors.toSet()));
            project.setOrganisation(testHelper.createNewOrganisation());
            projectsDao.persist(project);
            return project;
        });

        logger.info("Saved Project. Try to load it again.");

        jpaApi.withTransaction(() ->{
            Project foundProject = projectsDao.findById(saved.getId());

            logger.info("Check if saved correctly.");
            Zone first = foundProject.getZones().iterator().next();

            assertThat(first).isNotNull();
        });
    }
}