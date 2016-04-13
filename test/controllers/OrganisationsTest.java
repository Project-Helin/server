package controllers;

import commons.AbstractIntegrationTest;
import models.Organisation;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

public class OrganisationsTest extends AbstractIntegrationTest {

    @Test
    public void shouldShowNewOrganisation() {
        Organisation organisation = testHelper.createNewOrganisation();

        browser.goTo(routes.Organisations.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getId().toString());
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getName());
    }

    @Test
    public void shouldRemoveOrganisation() throws InterruptedException {
        Organisation organisation = testHelper.createNewOrganisation();

        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).contains(organisation.getName());
        // remove that
        browser.find("#delete-" + organisation.getId()).click();
        //confirm delete
        browser.find("#deleteconfirm-" + organisation.getId()).click();
        // verify
        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).doesNotContain(organisation.getName());
    }


    @Test
    public void shouldAddNewOrganisation() throws InterruptedException {
        browser.goTo(routes.Organisations.add().url());

        browser.fill(withName("name")).with("HSR Tester");
        browser.submit("Save");

        // verify
        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }

    @Test
    public void shouldUpdateOrganisation() {
        Organisation organisation = testHelper.createNewOrganisation();
        browser.goTo(routes.Organisations.index().url());

        // go to edit
        browser.find("#edit-" + organisation.getId()).click();

        // save it
        browser.fill(withName("name")).with("HSR Tester");
        browser.submit("Save");

        // verify
        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }


    //@Override
    //protected TestBrowser provideBrowser(int port) {
    //    return testBrowser(Helpers.FIREFOX);
    //}
}