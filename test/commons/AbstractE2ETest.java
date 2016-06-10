package service;

import com.google.inject.Inject;
import models.Organisation;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.Database;
import play.db.evolutions.Evolutions;
import play.db.jpa.JPAApi;
import play.inject.Injector;
import play.test.TestBrowser;
import play.test.WithBrowser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fluentlenium.core.filter.FilterConstructor.withName;
import static play.test.Helpers.*;

public abstract class AbstractE2ETest extends WithBrowser {

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

    @Override
    protected play.Application provideApplication() {
        Map<String, String> param = new HashMap<>();
        param.put("driver", "org.postgresql.Driver");
        param.put("url", "jdbc:postgresql://localhost:5455/test");
        param.put("username", "test");
        param.put("password", "test");

        return fakeApplication(param);
    }


    @Override
    protected TestBrowser provideBrowser(int port) {
        return testBrowser(FIREFOX);
    }

    protected void waitThreeSeconds() {
        browser.getDriver().manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }

    protected void waitFiveSeconds() {
        browser.getDriver().manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    /**
     * Wait for the element with given id be visible and click
     */
    protected void waitAndClick(String id) {
        WebDriverWait wait = new WebDriverWait(browser.getDriver(), 60);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        element.click();
    }

    protected void fillInRegisterForm(User user, String plainTextPassword) {
        browser.fill(withName("name")).with(user.getName());
        browser.fill(withName("email")).with(user.getEmail());
        browser.fill(withName("password")).with(plainTextPassword);

        browser.click("#register-user");
    }

    protected void fillInLoginForm(User user, String plainTextPassword) {
        browser.fill(withName("email")).with(user.getEmail());
        browser.fill(withName("password")).with(plainTextPassword);
        browser.submit("#login");
    }

    /**
     * Create new user and do login as that user.
     *
     * @return the organisation where the user was created
     */
    protected Organisation doLogin() {
        String password = "bla";

        Organisation organisation = jpaApi.withTransaction(em -> testHelper.createNewOrganisation());
        User user = jpaApi.withTransaction(em -> testHelper.createUserWithOrganisation(password, organisation));

        browser.goTo("/login");
        fillInLoginForm(user, password);

        return organisation;
    }
}
