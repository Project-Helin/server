package controllers;

import commons.AbstractE2ETest;
import models.Order;
import models.Organisation;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.fest.assertions.Assertions.assertThat;

public class OrdersControllerTest extends AbstractE2ETest {

    private Organisation organisation;

    @Before
    public void login() {
        organisation = doLogin();
    }

    @Test
    public void shouldShowNewOrderInAllOrderView() {
        Order order = jpaApi.withTransaction((em) -> {
            return testHelper.createNewOrder(
                testHelper.createNewProject(organisation),
                testHelper.createCustomer()
            );
        });

        browser.goTo(routes.OrdersController.index().url());

        // verify
        assertThat(browser.pageSource()).contains(order.getProject().getName());
        assertThat(browser.pageSource()).contains(order.getCustomer().getFamilyName());
    }

    @Test
    public void shouldNotShowOrderFromAnotherOrganisation() {
        Order order = jpaApi.withTransaction((em) -> {
            return testHelper.createNewOrder(
                testHelper.createNewProject(testHelper.createNewOrganisation()),
                testHelper.createCustomer("Peter", "Mueller")
            );
        });

        browser.goTo(routes.OrdersController.index().url());

        // verify
        assertThat(browser.pageSource()).doesNotContain(order.getProject().getName());
        assertThat(browser.pageSource()).doesNotContain(order.getCustomer().getFamilyName());
    }

    @Test
    public void shouldShowOrderPerProject() throws InterruptedException {
        Order order = jpaApi.withTransaction((em) -> {
            return testHelper.createNewOrder(
                testHelper.createNewProject(organisation),
                testHelper.createCustomer()
            );
        });

        browser.goTo(routes.OrdersController.index().url());
        browser.fillSelect("#projectList").withValue(order.getProject().getId().toString());

        // wait till
        WebDriverWait wait = new WebDriverWait(browser.getDriver(), 10);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("projectList")));
        element.click();

        // verify
        assertThat(browser.pageSource()).contains(order.getProject().getName());
        assertThat(browser.pageSource()).contains(order.getCustomer().getFamilyName());
    }

}