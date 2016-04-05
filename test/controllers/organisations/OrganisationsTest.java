package controllers.organisations;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.OrganisationsDao;
import models.Organisation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class OrganisationsTest extends AbstractIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(OrganisationsTest.class);

    @Inject
    private OrganisationsDao organisationsDao;

    @Test
    public void shouldShowNewOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setName("Super HSR " + System.currentTimeMillis());

        jpaapi.withTransaction(() -> {
            organisationsDao.persist(organisation);
        });

        browser.goTo(routes.Organisations.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getId().toString());
        assertThat(browser.pageSource()).containsIgnoringCase(organisation.getName());
    }




}