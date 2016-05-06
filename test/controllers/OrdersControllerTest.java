package controllers;

import commons.AbstractE2ETest;
import models.Order;
import models.Organisation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class OrdersControllerTest extends AbstractE2ETest {

    private Organisation organisation;

    @Before
    public void login() {
        organisation = doLogin();
    }

    @Test
    public void shouldShowNewOrderInAllOrderView() {
        Order order = testHelper.createNewOrder(
            testHelper.createNewProject(organisation),
            testHelper.createCustomer()
        );

        browser.goTo(routes.OrdersController.index().url());

        // verify
        assertThat(browser.pageSource()).contains(order.getProject().getName());
        assertThat(browser.pageSource()).contains(order.getCustomer().getDisplayName());
    }

    @Test
    public void shouldNotShowOrderFromAnotherOrganisation() {
        Order order = testHelper.createNewOrder(
            testHelper.createNewProject(testHelper.createNewOrganisation()),
            testHelper.createCustomer()
        );

        browser.goTo(routes.OrdersController.index().url());

        // verify
        assertThat(browser.pageSource()).doesNotContain(order.getProject().getName());
        assertThat(browser.pageSource()).doesNotContain(order.getCustomer().getDisplayName());
    }

    @Test
    @Ignore
    public void shouldShowOrderPerProject() throws InterruptedException {
        Order order = testHelper.createNewOrder(
            testHelper.createNewProject(organisation),
            testHelper.createCustomer()
        );

        browser.goTo(routes.OrdersController.index().url());
        browser.click("#projectList");
        browser.click("#project-" + order.getProject().getId().toString());

        // verify
        assertThat(browser.pageSource()).doesNotContain(order.getProject().getName());
        assertThat(browser.pageSource()).doesNotContain(order.getCustomer().getDisplayName());
    }

}