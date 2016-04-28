package controllers;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.UserDao;
import models.User;
import org.junit.Test;
import play.i18n.Messages;

import static org.fest.assertions.Assertions.assertThat;

public class UsersControllerTest extends AbstractE2ETest {

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
        browser.click("#register");

        fillInRegisterForm(user, plainTextPassword);

        assertThat(browser.pageSource()).contains("Log in");

        fillInLoginForm(user, plainTextPassword);

        assertThat(browser.pageSource()).contains(user.getName());
    }


    @Test
    public void registerUserWithoutEmail() {
        User userWithoutEmail = new User();
        userWithoutEmail.setName("Anna Bolika");
        userWithoutEmail.setEmail("");
        userWithoutEmail.setPassword(plainTextPassword);

        browser.goTo(routes.UsersController.add().url());

        browser.click("#register");

        fillInRegisterForm(userWithoutEmail, plainTextPassword);

        assertThat(browser.pageSource()).containsIgnoringCase(Messages.get("error.required"));
        assertThat(browser.pageSource()).containsIgnoringCase("Register");
        assertThat(browser.pageSource()).contains("Login");
    }

    @Test
    public void login() {

        User user = testHelper.createUserWithOrganisation(plainTextPassword);

        browser.goTo(routes.UsersController.login().url());
        assertThat(browser.pageSource()).contains("Login");

        fillInLoginForm(user, plainTextPassword);

        assertThat(browser.pageSource()).contains(user.getName());
    }

    @Test
    public void loginWithWrongData() {

        User user = testHelper.createUserWithOrganisation(plainTextPassword);

        browser.goTo(routes.UsersController.login().url());

        browser.submit("#login");
        assertThat(browser.pageSource()).doesNotContain(user.getName());

        user.setEmail("wrong.email@example.com");
        fillInLoginForm(user, plainTextPassword);

        assertThat(browser.pageSource()).doesNotContain(user.getName());
        assertThat(browser.pageSource()).containsIgnoringCase("wrong user or password");
    }

}