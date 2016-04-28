package controllers;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.OrganisationsDao;
import models.Organisation;
import models.User;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

public class OrganisationsControllerTest extends AbstractE2ETest {


    @Inject
    private OrganisationsDao organisationsDao;

    private void login() {
        String password = "bla";
        User user = testHelper.createUserWithOrganisation(password);
        browser.goTo("/login");
        fillInLoginForm(user, password);
    }

    @Test
    public void shouldAddNewOrganisation() throws InterruptedException {
        login();
        browser.goTo(routes.OrganisationsController.add().url());

        browser.fill(withName("name")).with("HSR Tester");
        browser.click("#save");

        // verify
        jpaApi.withTransaction(() -> {
            List<Organisation> found = organisationsDao.findAll();
            List<String> names = found.stream().map(Organisation::getName).collect(Collectors.toList());

            assertThat(names).contains("HSR Tester");
        });
    }

    @Test
    public void shouldUpdateOrganisation() {
        login();
        browser.goTo(routes.OrganisationsController.edit().url());

        // save it
        browser.fill(withName("name")).with("HSR Test Organisation");
        browser.click("#save");

        // verify
        jpaApi.withTransaction(() -> {
            List<Organisation> found = organisationsDao.findAll();
            List<String> names = found.stream().map(Organisation::getName).collect(Collectors.toList());

            assertThat(names).contains("HSR Test Organisation");
        });
    }
}