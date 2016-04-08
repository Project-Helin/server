package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.UserDao;
import models.User;
import org.junit.Test;
import play.test.Helpers;
import play.test.TestBrowser;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;
import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static play.test.Helpers.testBrowser;

public class UsersControllerTest extends AbstractIntegrationTest {

    @Inject
    private UserDao userDao;

    String plainTextPassword = "test-pasasdf ksajd f sword";

    @Test
    public void registerUser() {
        User user = new User();
        user.setName("Anna Bolika");
        user.setEmail("anna.bolika@example.com");
        user.setPassword(plainTextPassword);

        browser.goTo("/");

        browser.click(withText("Register"));

        assertThat(browser.pageSource()).containsIgnoringCase("Register");

        fillInRegisterForm(user, plainTextPassword);

        assertThat(browser.pageSource()).contains("Log in");
    }

    @Test
    public void login() {

        User user = createUser();

        browser.goTo(routes.UsersController.login().url());
        assertThat(browser.pageSource()).contains("Login");

        fillInLoginForm(user, plainTextPassword);

        assertThat(browser.pageSource()).contains(user.getName());
    }

    @Test
    public void loginWithWrongData() {

        User user = createUser();

        browser.goTo(routes.UsersController.login().url());

        browser.submit("#login");
        assertThat(browser.pageSource()).doesNotContain(user.getName());

        user.setEmail("wrong.email@example.com");
        fillInLoginForm(user, plainTextPassword);

        assertThat(browser.pageSource()).doesNotContain(user.getName());
        assertThat(browser.pageSource()).containsIgnoringCase("wrong user or password");
    }

    private void fillInRegisterForm(User user, String plainTextPassword) {
        String randomString = UUID.randomUUID().toString();
        browser.fill(withName("name")).with(user.getName() + randomString);
        browser.fill(withName("email")).with(user.getEmail() + randomString);
        browser.fill(withName("password")).with(plainTextPassword);
        browser.submit("#register");
    }

    private void fillInLoginForm(User user, String plainTextPassword) {
        browser.fill(withName("email")).with(user.getEmail());
        browser.fill(withName("password")).with(plainTextPassword);
        browser.submit("#login");
    }
    private User createUser() {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setName("Anna Bolika");
        user.setEmail("anna.bolika@example.com");
        user.setPassword(plainTextPassword);

        jpaapi.withTransaction(() -> {
            userDao.persist(user);
        });

        return user;
    }

    @Override
    protected TestBrowser provideBrowser(int port) {
        return testBrowser(Helpers.FIREFOX);
    }
}