package commons;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.Databases;
import play.db.evolutions.Evolutions;

public abstract class AbstractIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected play.db.Database database;

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

}
