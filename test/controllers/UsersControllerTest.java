package controllers;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.UserDao;
import models.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.i18n.Messages;

import static org.fest.assertions.Assertions.assertThat;

public class UsersControllerTest extends AbstractE2ETest {

    private static final String PLAIN_TEXT_PASSWORD = "test-pasasdf ksajd f sword";

    @Inject
    private UserDao userDao;

    @Test
    public void registerUser() {
        User user = new User();
        user.setName("Bruce Wayne");
        user.setEmail("batman@wayneenterprise");
        user.setPassword(PLAIN_TEXT_PASSWORD);

        browser.goTo("/");
        browser.click("#register");

        fillInRegisterForm(user, PLAIN_TEXT_PASSWORD);

        assertThat(browser.pageSource()).contains("Log in");

        fillInLoginForm(user, PLAIN_TEXT_PASSWORD);

        assertThat(browser.pageSource()).contains(user.getName());
    }

    @Test
    public void registerUserWithSameEmailTwice() {
        User existingUser = new User();
        existingUser.setConfirmationToken(RandomStringUtils.random(10));
        existingUser.setName("Burce Wayne");
        existingUser.setEmail("batman@example.com");
        existingUser.setPassword(PLAIN_TEXT_PASSWORD);
        jpaApi.withTransaction(() -> userDao.persist(existingUser));

        browser.goTo("/");
        browser.click("#register");

        User newUserWithSameEmail = new User();
        newUserWithSameEmail.setName("Batman B.");
        newUserWithSameEmail.setEmail("batman@example.com");
        newUserWithSameEmail.setPassword(PLAIN_TEXT_PASSWORD);

        fillInRegisterForm(newUserWithSameEmail, PLAIN_TEXT_PASSWORD);

        assertThat(browser.pageSource()).contains("Email address is already taken");
    }

    @Test
    public void registerUserWithoutEmail() {
        User userWithoutEmail = new User();
        userWithoutEmail.setName("Bruce Wayne");
        userWithoutEmail.setEmail("");
        userWithoutEmail.setPassword(PLAIN_TEXT_PASSWORD);

        browser.goTo(routes.UsersController.add().url());

        browser.click("#register");

        fillInRegisterForm(userWithoutEmail, PLAIN_TEXT_PASSWORD);

        assertThat(browser.pageSource()).containsIgnoringCase(Messages.get("error.required"));
        assertThat(browser.pageSource()).containsIgnoringCase("Register");
        assertThat(browser.pageSource()).contains("Login");
    }

    @Test
    public void login() {

        User user = jpaApi.withTransaction(em -> testHelper.createUserWithOrganisation(PLAIN_TEXT_PASSWORD));

        browser.goTo(routes.UsersController.login().url());
        assertThat(browser.pageSource()).contains("Login");

        fillInLoginForm(user, PLAIN_TEXT_PASSWORD);

        assertThat(browser.pageSource()).contains(user.getName());
    }

    @Test
    public void loginWithWrongData() {

        User user = jpaApi.withTransaction(em -> testHelper.createUserWithOrganisation(PLAIN_TEXT_PASSWORD));

        browser.goTo(routes.UsersController.login().url());

        browser.submit("#login");
        assertThat(browser.pageSource()).doesNotContain(user.getName());

        user.setEmail("wrong.email@example.com");
        fillInLoginForm(user, PLAIN_TEXT_PASSWORD);

        assertThat(browser.pageSource()).doesNotContain(user.getName());
        assertThat(browser.pageSource()).containsIgnoringCase("wrong user or password");
    }

}