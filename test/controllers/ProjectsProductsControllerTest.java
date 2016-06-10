package controllers;

import com.google.inject.Inject;
import service.AbstractE2ETest;
import dao.ProjectsDao;
import models.Organisation;
import models.Product;
import models.Project;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withId;

public class ProjectsProductsControllerTest extends AbstractE2ETest {

    @Inject
    private ProjectsDao projectsDao;

    private Organisation organisation;

    @Before
    public void login() {
        organisation = doLogin();
    }

    @Test
    public void shouldShowProductInProject() {
        Project project = jpaApi.withTransaction(em -> {
            Product newProduct = testHelper.createProduct(organisation);
            return testHelper.createNewProject(organisation, newProduct);
        });

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-products-" + project.getId());

        // verify
        Product newProduct = project.getProducts().iterator().next();
        assertThat(browser.pageSource()).contains(newProduct.getName());
    }

    @Test
    public void shouldNotShowProductFromOtherOrganisation() {
        Product productFromOtherOrganisation = jpaApi.withTransaction(em ->
            testHelper.createProduct(testHelper.createNewOrganisation())
        );
        Project project = jpaApi.withTransaction(em -> testHelper.createNewProject(organisation));

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-products-" + project.getId());

        // verify
        assertThat(browser.pageSource()).doesNotContain(productFromOtherOrganisation.getName());
    }

    @Test
    public void shouldAddProductToProject() {
        Product productToAdd = jpaApi.withTransaction(em -> testHelper.createProduct(organisation));
        Project project = jpaApi.withTransaction(em -> testHelper.createNewProject(organisation));

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-products-" + project.getId());

        // add product
        browser.click("#add");

        // verify in db
        jpaApi.withTransaction(() -> {
            Project found = projectsDao.findById(project.getId());
            assertThat(found.getProducts()).hasSize(1);

            Product first = found.getProducts().iterator().next();
            assertThat(first.getName()).isEqualTo(productToAdd.getName());
        });
    }

    @Test
    public void shouldDeleteProductFromProject() {
        final Product[] productToDelete = new Product[1];
        Project project = jpaApi.withTransaction(em -> {
            productToDelete[0] = testHelper.createProduct(organisation);
            return testHelper.createNewProject(organisation, productToDelete[0]);
        });

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-products-" + project.getId());

        // delete product
        browser.click(withId("delete-" + productToDelete[0].getId().toString()));
        waitAndClick("deleteconfirm-" + productToDelete[0].getId().toString());
        waitFiveSeconds();

        // verify in db
        jpaApi.withTransaction(() -> {
            Project found = projectsDao.findById(project.getId());
            assertThat(found.getProducts()).isEmpty();
        });
    }
}