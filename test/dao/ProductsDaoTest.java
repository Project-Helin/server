package dao;

import com.google.inject.Inject;
import service.AbstractIntegrationTest;
import dto.api.ProductApiDto;
import models.Organisation;
import models.Product;
import models.Project;
import models.ZoneType;
import org.junit.Test;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fest.assertions.Assertions.assertThat;

public class ProductsDaoTest extends AbstractIntegrationTest {

    @Inject
    private ProductsDao productsDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private JPAApi jpaApi;

    @Test
    public void shouldFindProductByPosition() {
        jpaApi.withTransaction(() -> {
            Organisation organisation = testHelper.createNewOrganisation();

            Project project = testHelper.createNewProject(organisation,
                testHelper.createUnsavedZone(
                    "Orderzone Zone",
                    ZoneType.OrderZone,
                    testHelper.createSamplePolygon()
                )
            );
            Product product = testHelper.createProduct(organisation);
            project.setProducts(Stream.of(product).collect(Collectors.toSet()));
            product.setProjects(Stream.of(project).collect(Collectors.toSet()));
            projectsDao.persist(project);

            // (30, 30) is withing the above delivery zone
            List<ProductApiDto> byPosition = productsDao.findByPosition(30d, 30d);
            assertThat(byPosition).hasSize(1);
            assertThat(byPosition.get(0).getId()).isEqualTo(product.getIdAsString());
            assertThat(byPosition.get(0).getName()).isEqualTo(product.getName());
        });
    }
}