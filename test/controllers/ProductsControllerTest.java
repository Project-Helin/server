package controllers;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.ProductsDao;
import models.Organisation;
import models.Product;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static controllers.routes.ProjectsController;
import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withId;

public class ProductsControllerTest extends AbstractE2ETest {

    @Inject
    private ProductsDao productsDao;

    private Organisation currentOrganisation;

    @Before
    public void login() {
        currentOrganisation = doLogin();
    }

    @Test
    public void shouldShowNewProduct() {
        Product newProduct = jpaApi.withTransaction(em -> {
            return testHelper.createProduct(currentOrganisation, 99);
        });

        browser.goTo(routes.ProductsController.index().url());

        // verify
        assertThat(browser.pageSource()).contains(newProduct.getId().toString());
        assertThat(browser.pageSource()).contains(newProduct.getName());
        assertThat(browser.pageSource()).contains(String.valueOf(newProduct.getPrice()));
        assertThat(browser.pageSource()).contains(String.valueOf(newProduct.getWeightGramm()));
        assertThat(browser.pageSource()).contains(String.valueOf(newProduct.getMaxItemPerDrone()));
    }

    @Test
    public void shouldRemoveProduct() {
        Product product = jpaApi.withTransaction(em -> {
            return testHelper.createProduct(currentOrganisation);
        });

        // go to table
        browser.goTo(routes.ProductsController.index().url());

        // verify
        assertThat(browser.pageSource()).contains(product.getId().toString());

        // click remove button
        browser.find("#delete-" + product.getId()).click();
        //confirm delete
        waitAndClick("deleteconfirm-" + product.getId());
        waitFiveSeconds();

        // verify
        browser.goTo(ProjectsController.index().url());
        assertThat(browser.pageSource()).doesNotContain(product.getName());
    }

    @Test
    public void shouldAddNewProduct() {
        browser.goTo(routes.ProductsController.add().url());

        browser.fill(withId("Name")).with("Kaboom");
        browser.fill(withId("Price")).with("100");
        browser.fill(withId("weightGramm")).with("300");
        browser.fill(withId("maxItemPerDrone")).with("99");
        browser.click("#save");

        // verify
        List<Product> all = jpaApi.withTransaction((e) -> {
            return productsDao.findAll();
        });
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("Kaboom");
        assertThat(all.get(0).getPrice()).isEqualTo(100);
        assertThat(all.get(0).getWeightGramm()).isEqualTo(300);
        assertThat(all.get(0).getMaxItemPerDrone()).isEqualTo(99);
    }

    @Test
    public void shouldUpdateProduct() {
        Product product = jpaApi.withTransaction(em -> {
            return testHelper.createProduct(currentOrganisation);
        });
        browser.goTo(routes.ProductsController.index().url());

        // go to edit
        browser.click("#edit-" + product.getId());

        // save it
        browser.fill(withId("Name")).with("HolaHola");
        browser.fill(withId("maxItemPerDrone")).with("123");
        browser.click("#save");

        // verify
        List<Product> all = jpaApi.withTransaction((e) -> {
            return productsDao.findAll();
        });
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("HolaHola");
        assertThat(all.get(0).getMaxItemPerDrone()).isEqualTo(123);
    }
}