package controllers.organisations;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.OrganisationsDao;
import models.Organisation;
import org.junit.Test;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class OrganisationsTest extends AbstractIntegrationTest {
    @Inject
    private OrganisationsDao organisationsDao;

    @Test
    public void shouldShowNewOrganisation() {
        Organisation organisation = createNewOrganisation();

        browser.goTo(routes.Organisations.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getId().toString());
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getName());
    }

    @Test
    public void shouldRemoveOrganisation() {
        Organisation organisation = createNewOrganisation();

        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).contains(organisation.getName());

        // remove that
        browser.click(withText("Delete"));

        // verify
        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).doesNotContain(organisation.getName());
    }

    private Organisation createNewOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setName("Super HSR " + System.currentTimeMillis());

        jpaapi.withTransaction(() -> {
            organisationsDao.persist(organisation);
        });
        return organisation;
    }

    @Test
    public void shouldAddNewOrganisation() {
        browser.goTo(routes.Organisations.add().url());

        browser.fill(withName("Name")).with("HSR Tester");
        browser.submit("Save");

        // verify
        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }

    @Test
    public void shouldUpdateOrganisation() {
        Organisation organisation = createNewOrganisation();
        browser.goTo(routes.Organisations.index().url());

        // go to edit
        browser.click(withText("Edit"));

        // save it
        browser.fill(withName("Name")).with("HSR Tester");
        browser.submit("Save");

        // verify
        browser.goTo(routes.Organisations.index().url());
        assertThat(browser.pageSource()).doesNotContain("HSR Tester");
    }
}