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
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;

import java.util.Arrays;
import java.util.HashSet;

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
    private ImprovedTestHelper improvedTestHelper;

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

    @Test
    public void bla() {

        Project saved = jpaApi.withTransaction((em) -> {
            Project project = new Project();
            project.setName("First Demo");
            Zone ja = testHelper.createUnsavedZone("ja", ZoneType.DeliveryZone);
            ja.setProject(project);
            project.setZones(new HashSet<>(Arrays.asList(ja)));
            project.setOrganisation(improvedTestHelper.createNewOrganisation());
            projectsDao.persist(project);

            return project;
        });

        System.out.println("=> saved ");

        jpaApi.withTransaction(() ->{
            Project byId = projectsDao.findById(saved.getId());

            System.out.println("=> check if saved");
            Zone first = byId.getZones().iterator().next();
            first.setType(ZoneType.LoadingZone);

        });


        System.out.println("=> done");
    }
}