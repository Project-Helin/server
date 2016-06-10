package controllers;

import commons.AbstractE2ETest;
import models.Customer;
import models.Organisation;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class CustomersControllerTest extends AbstractE2ETest {

    private Organisation organisation;

    @Before
    public void login() {
        organisation = doLogin();
    }

    @Test
    public void shouldNotSeeCustomerWithoutProject() {
        Customer customer = jpaApi.withTransaction((em) -> {
            return testHelper.createCustomer("Peter", "Mueller");
        });

        browser.goTo(routes.CustomersController.index().url());

        // verify
        assertThat(browser.pageSource()).doesNotContain(customer.getFamilyName());
        assertThat(browser.pageSource()).doesNotContain(customer.getGivenName());
        assertThat(browser.pageSource()).doesNotContain(customer.getEmail());
    }

    @Test
    public void shouldShowCustomerFromCurrentOrganisation() {
        Customer customer = jpaApi.withTransaction((em) -> {
            Customer customerInDb = testHelper.createCustomer("Peter", "Mueller");
            testHelper.createNewOrder(
                testHelper.createNewProject(organisation),
                customerInDb
            );
            return customerInDb;
        });

        browser.goTo(routes.CustomersController.index().url());

        // verify
        assertThat(browser.pageSource()).contains(customer.getFamilyName());
        assertThat(browser.pageSource()).contains(customer.getGivenName());
        assertThat(browser.pageSource()).contains(customer.getEmail());
    }

    @Test
    public void shouldNotSeeCustomerFromOtherOrganisation() {
        Customer customer = jpaApi.withTransaction((em) -> {
            Customer customerInDb = testHelper.createCustomer("Peter", "Mueller");
            testHelper.createNewOrder(
                testHelper.createNewProject(testHelper.createNewOrganisation()),
                customerInDb
            );
            return customerInDb;
        });

        browser.goTo(routes.CustomersController.index().url());

        // verify
        assertThat(browser.pageSource()).doesNotContain(customer.getFamilyName());
        assertThat(browser.pageSource()).doesNotContain(customer.getGivenName());
        assertThat(browser.pageSource()).doesNotContain(customer.getEmail());
    }
}