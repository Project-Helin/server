package commons;

import com.google.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.Database;
import play.db.evolutions.Evolutions;
import play.db.jpa.JPAApi;
import play.inject.Injector;
import play.test.WithApplication;

public abstract class AbstractIntegrationTest extends WithApplication {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    protected JPAApi jpaApi;

    @Inject
    private Database database;

    @Inject
    protected ImprovedTestHelper testHelper;

    @Before
    public void setupDatabaseAndHelper() {
        Injector playInjector = app.injector();
        /**
         * Stupid Play - why don't they provide the real injector - or even provide some
         * useful methods.
         *
         * So the problem is, that the play injector doesn't provide the injectMember() method
         * from the original Guice Injector. So we need to get the 'real' Guice injector to do that.
         */
        com.google.inject.Injector guiceInjector =
            playInjector.instanceOf(com.google.inject.Injector.class);
        guiceInjector.injectMembers(this);

        logger.info("Apply Evolutions");
        Evolutions.applyEvolutions(database);
    }

    @After
    public void shutdownDatabase() {
        logger.info("Cleanup Evolutions");
        Evolutions.cleanupEvolutions(database);
    }
}
