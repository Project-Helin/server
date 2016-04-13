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
import play.test.WithBrowser;

import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.fakeApplication;

public abstract class AbstractIntegrationTest extends WithBrowser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    protected JPAApi jpaApi;

    @Inject
    private Database database;

    @Inject
    protected TestHelper testHelper;

    @Before
    public void setupDatabaseAndHelper() {
        Injector playInjector = app.injector();
        /**
         * Stupid Play - why don't the provide the real injector - or even provide some
         * useful methods.
         *
         * So the problem is, that the play injector doesn't provide the injectMember() method.
         * So we need to get the 'real' Guice injector to do that.
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

    @Override
    protected play.Application provideApplication() {
        Map<String, String> param = new HashMap<>();
        param.put("driver", "org.postgresql.Driver");
        param.put("url", "jdbc:postgresql://localhost:5432/test");
        param.put("username", "test");
        param.put("password", "test");

        return fakeApplication(param);
    }
}
