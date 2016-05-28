package controllers.api;

import ch.helin.messages.dto.ProductDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import commons.AbstractWebServiceIntegrationTest;
import dto.api.OrganisationApiDto;
import dto.api.ProductApiDto;
import models.Organisation;
import models.Product;
import models.Project;
import models.ZoneType;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

        OrganisationApiDto organisation = foundProduct.getOrganisation();
        assertThat(organisation.getId()).isEqualTo(firstProduct.getOrganisation().getIdAsString());
        assertThat(organisation.getName()).isEqualTo(firstProduct.getOrganisation().getName());
    }

    @Test
    public void shouldNotFindProductByLocationBecauseOutsideOfOrderZone() throws IOException {
        createProduct();

        // (40, 0) is outside of the order zone
        List<ProductApiDto> list = apiHelper.doGetWithListResponse(
            routes.ProductsApiController.findByLocation(40, 0),
            new TypeReference<List<ProductApiDto>>() {}
        );

        assertThat(list).isEmpty();
    }


    @Test
    public void shouldFindProductByLocationProject() {
        Product product = createProduct();

        Project project = product.getProjects().stream().findFirst().get();

        List<ProductDto> foundProducts = apiHelper.doGetWithListResponse(
            routes.ProductsApiController.findByProject(project.getIdAsString()),
            new TypeReference<List<ProductDto>>() {}
        );

        assertThat(foundProducts).hasSize(1);

        ProductDto foundProduct = foundProducts.get(0);
        assertThat(foundProduct.getId()).isEqualTo(product.getId());
        assertThat(foundProduct.getName()).isEqualTo(product.getName());
        assertThat(foundProduct.getPrice()).isEqualTo(product.getPrice());
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