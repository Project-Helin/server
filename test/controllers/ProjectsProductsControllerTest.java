package controllers;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.ProjectsDao;
import models.Organisation;
import models.Product;
import models.Project;
import models.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.UUID;

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
        Product newProduct = testHelper.createProduct(organisation);
        newProduct.setId(UUID.randomUUID());
        Project project = testHelper.createNewProject(organisation, newProduct);

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-products-" + project.getId());

        // verify
        assertThat(browser.pageSource()).contains(newProduct.getName());
    }

    @Test
    public void shouldNotShowProductFromOtherOrganisation() {
        Product productFromOtherOrganisation =
            testHelper.createProduct(testHelper.createNewOrganisation());
        Project project = testHelper.createNewProject(organisation);

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-products-" + project.getId());

        // verify
        assertThat(browser.pageSource()).doesNotContain(productFromOtherOrganisation.getName());
    }

    @Test
    public void shouldAddProductToProject() {
        Product productToAdd = testHelper.createProduct(organisation);
        productToAdd.setId(UUID.randomUUID());

        Project project = testHelper.createNewProject(organisation);

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
        Product productToDelete = testHelper.createProduct(organisation);
        productToDelete.setId(UUID.randomUUID());

        Project project = testHelper.createNewProject(organisation, productToDelete);

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-products-" + project.getId());

        // delete product
        browser.click(withId("delete-" + productToDelete.getId().toString()));
        waitAndClick("deleteconfirm-" + productToDelete.getId().toString());
        waitFiveSeconds();

        // verify in db
        jpaApi.withTransaction(() -> {
            Project found = projectsDao.findById(project.getId());
            assertThat(found.getProducts()).isEmpty();
        });
    }
}