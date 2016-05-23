package controllers.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import dto.api.ProductApiDto;
import models.Organisation;
import models.Product;
import models.Project;
import models.ZoneType;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fest.assertions.Assertions.assertThat;

public class ProductsApiControllerTest extends AbstractWebServiceIntegrationTest {

    @Inject
    private ApiHelper apiHelper;

    @Test
    public void shouldFindProductByLocationWithinOrderZone() throws IOException {
        Product product = createProduct();

        // (30, 30) is within the above order zone
        List<ProductApiDto> list = apiHelper.doGetWithListResponse(
            routes.ProductsApiController.findByLocation(30, 30),
            new TypeReference<List<ProductApiDto>>() {}
        );

        assertThat(list).hasSize(1);

        ProductApiDto foundProduct = list.get(0);
        Project firstProduct =  product.getProjects().stream().findFirst().get();

        assertThat(foundProduct.getId()).isEqualTo(product.getIdAsString());
        assertThat(foundProduct.getName()).isEqualTo(product.getName());
        assertThat(foundProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(foundProduct.getProjectId()).isEqualTo(firstProduct.getIdAsString());
        assertThat(foundProduct.getOrganisationId()).isEqualTo(firstProduct.getOrganisation().getIdAsString());
    }

    @Test
    public void shouldNotFindProductByLocationBecasueOutsideOfOrderZone() throws IOException {
        createProduct();

        // (40, 0) is outside of the order zone
        List<ProductApiDto> list = apiHelper.doGetWithListResponse(
            routes.ProductsApiController.findByLocation(40, 0),
            new TypeReference<List<ProductApiDto>>() {}
        );

        assertThat(list).isEmpty();
    }

    private Product createProduct() {
        return jpaApi.withTransaction((em) -> {
                Organisation organisation = testHelper.createNewOrganisation();

                Project project = testHelper.createNewProject(
                    organisation,
                    testHelper.createUnsavedZone(
                        "MyTest",
                        ZoneType.OrderZone,
                        testHelper.createSamplePolygon()
                    )
                );

                Product productInDb = testHelper.createProduct(organisation);

                project.setProducts(Stream.of(productInDb).collect(Collectors.toSet()));
                productInDb.setProjects(Stream.of(project).collect(Collectors.toSet()));

                return productInDb;
            });
    }
}