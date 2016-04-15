package controllers;

import commons.AbstractIntegrationTest;
import models.Organisation;
import models.User;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

public class OrganisationsControllerTest extends AbstractIntegrationTest {


    private User user;
    private Organisation organisation;


    private void login() {
        String password = "bla";
        user = testHelper.createUserWithOrganisation(password);
        organisation = user.getOrganisations().stream().findFirst().get();
        browser.goTo("/login");
        fillInLoginForm(user, password);
    }

    @Test
    public void shouldAddNewOrganisation() throws InterruptedException {
        login();
        browser.goTo(routes.OrganisationsController.add().url());

        browser.fill(withName("name")).with("HSR Tester");
        browser.submit("Save");

        // verify
        browser.goTo(routes.OrganisationsController.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }

    @Test
    public void shouldUpdateOrganisation() {
        login();
        browser.goTo(routes.OrganisationsController.edit().url());

        // save it
        browser.fill(withName("name")).with("HSR Test Organisation");
        browser.submit("Save");

        // verify
        browser.goTo(routes.OrganisationsController.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }
}