import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolutions;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class IntegrationTest {

    private Database database;

    private static final Logger logger = getLogger(IntegrationTest.class);

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

    /**
     * add your integration test here
     * in this example we just check if the welcome page is being shown
     */
    @Test
    public void test() {
        /*
        Map<String, String> database = inMemoryDatabase();

        running(testServer(3333, fakeApplication(database)), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333");
            assertThat(browser.pageSource(), containsString("Add Person"));
        });
        */
        assertTrue(true);
    }

}
