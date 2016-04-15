package controllers;

import commons.AbstractIntegrationTest;
import models.Organisation;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

public class OrganisationsControllerTest extends AbstractIntegrationTest {

    @Test
    public void shouldShowNewOrganisation() {
        Organisation organisation = testHelper.createNewOrganisation();

        browser.goTo(routes.OrganisationsController.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getId().toString());
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getName());
    }

    @Test
    public void shouldRemoveOrganisation() throws InterruptedException {
        Organisation organisation = testHelper.createNewOrganisation();

        browser.goTo(routes.OrganisationsController.index().url());
        assertThat(browser.pageSource()).contains(organisation.getName());
        // remove that
        browser.find("#delete-" + organisation.getId()).click();
        //confirm delete
        waitAndClick("deleteconfirm-" + organisation.getId());
        waitFiveSeconds();

        // verify
        browser.goTo(routes.OrganisationsController.index().url());
        assertThat(browser.pageSource()).doesNotContain(organisation.getName());

    }


    @Test
    public void shouldAddNewOrganisation() throws InterruptedException {
        browser.goTo(routes.OrganisationsController.add().url());

        browser.fill(withName("name")).with("HSR Tester");
        browser.submit("Save");

        // verify
        browser.goTo(routes.OrganisationsController.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }

    @Test
    public void shouldUpdateOrganisation() {
        Organisation organisation = testHelper.createNewOrganisation();
        browser.goTo(routes.OrganisationsController.index().url());

        // go to edit
        browser.find("#edit-" + organisation.getId()).click();

        // save it
        browser.fill(withName("name")).with("HSR Tester");
        browser.submit("Save");

        // verify
        browser.goTo(routes.OrganisationsController.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }


    //@Override
    //protected TestBrowser provideBrowser(int port) {
    //    return testBrowser(Helpers.FIREFOX);
    //}
}