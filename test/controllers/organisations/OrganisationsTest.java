package controllers.organisations;

import com.google.common.collect.ImmutableMap;
import dao.OrganisationsDao;
import models.Organisation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolutions;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class OrganisationsTest {

    private OrganisationsDao organisationsDao = new OrganisationsDao();

    private static final Logger logger =
            LoggerFactory.getLogger(OrganisationsTest.class);
    private Database database;

    @Before
    public void createDatabase() {
        logger.info("Create Database");
        database = Databases.createFrom(
                "org.postgresql.Driver",
                "jdbc:postgresql://localhost:5432/test",
                ImmutableMap.of(
                        "username", "test",
                        "password", "test"
                )
        );

        logger.info("Apply Evolutions");
        Evolutions.applyEvolutions(database);
    }

    @After
    public void shutdownDatabase() {
        logger.info("Cleanup Evolutions");
        Evolutions.cleanupEvolutions(database);

        logger.info("Shutdown database");
        database.shutdown();
    }

    @Test
    public void shouldListAllOrganisations() {

        Map<String, String> param = new HashMap<>();
        param.put("driver", "org.postgresql.Driver");
        param.put("url", "jdbc:postgresql://localhost:5432/test");
        param.put("username", "test");
        param.put("password", "test");

        FakeApplication app = fakeApplication(param);
        Helpers.start(app);

        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setName("Super HSR " + System.currentTimeMillis());

        JPA.withTransaction(() -> organisationsDao.persist(organisation));

        running(testServer(3333, app), FIREFOX, browser -> {

            browser.goTo("http://localhost:3333" + routes.Organisations.index().url());

            // assertThat(browser.pageSource()).containsIgnoringCase(organisation.getName());
            assertThat(browser.pageSource()).containsIgnoringCase(organisation.getId().toString());

        });

        Helpers.stop(app);
    }
}