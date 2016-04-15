package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.ProductsDao;
import models.Product;
import org.junit.Test;

import java.util.List;

import static controllers.routes.OrganisationsController;
import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withId;

public class ProductsControllerTest extends AbstractIntegrationTest {

    @Inject
    private ProductsDao productsDao;

    @Test
    public void shouldShowNewProduct() {
        Product newProduct = testHelper.createProduct();

        browser.goTo(routes.ProductsController.index().url());

        // verify
        assertThat(browser.pageSource()).contains(newProduct.getId().toString());
        assertThat(browser.pageSource()).contains(newProduct.getName());
        assertThat(browser.pageSource()).contains(String.valueOf(newProduct.getPrice()));
        assertThat(browser.pageSource()).contains(String.valueOf(newProduct.getWightGramm()));
    }

    @Test
    public void shouldRemoveProduct() {
        Product product = testHelper.createProduct();

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
        browser.goTo(OrganisationsController.index().url());
        assertThat(browser.pageSource()).doesNotContain(product.getName());
    }

    @Test
    public void shouldAddNewProduct() {
        browser.goTo(routes.ProductsController.add().url());

        browser.fill(withId("Name")).with("Kaboom");
        browser.fill(withId("Price")).with("100");
        browser.fill(withId("wightGramm")).with("300");
        browser.click("#save");

        // verify
        List<Product> all = jpaApi.withTransaction((e) -> {
            return productsDao.findAll();
        });
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("Kaboom");
        assertThat(all.get(0).getPrice()).isEqualTo(100);
        assertThat(all.get(0).getWightGramm()).isEqualTo(300);
    }


    @Test
    public void shouldUpdateProduct() {
        Product product = testHelper.createProduct();
        browser.goTo(routes.ProductsController.index().url());

        // go to edit
        browser.click("#edit-" + product.getId());

        // save it
        browser.fill(withId("Name")).with("HolaHola");
        browser.click("#save");

        // verify
        List<Product> all = jpaApi.withTransaction((e) -> {
            return productsDao.findAll();
        });
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("HolaHola");
    }
}