package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.DroneDao;
import models.Drone;
import models.Organisation;
import models.User;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

public class DronesControllerTest extends AbstractIntegrationTest {
    @Inject
    private DroneDao droneDao;

    private User user;
    private Organisation organisation;


    @Before
    public void login() {
        String password = "bla";
        user = testHelper.createUserWithOrganisation(password);
        organisation = user.getOrganisations().stream().findFirst().get();
        browser.goTo("/login");
        fillInLoginForm(user, password);
    }

    @Test
    public void shouldShowNewDrone() {
        organisation = user.getOrganisations().stream().findFirst().get();
        Drone drone = testHelper.createNewDrone(organisation);

        browser.goTo(routes.DronesController.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getId().toString());
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getName());
        assertThat(browser.pageSource()).containsIgnoringCase(String.valueOf(drone.getPayload()));
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getToken().toString());
    }

    @Test
    public void shouldRemoveDrone() throws InterruptedException {
        Drone drone = testHelper.createNewDrone(organisation);

        browser.goTo(routes.DronesController.index().url());
        assertThat(browser.pageSource()).contains(drone.getName());
        // remove that
        waitAndClick("delete-" + drone.getId());

        //confirm delete
        waitAndClick("deleteconfirm-" + drone.getId());
        waitFiveSeconds();

        // verify
        browser.goTo(routes.DronesController.index().url());
        assertThat(browser.pageSource()).doesNotContain(drone.getName());
    }


    @Test
    public void shouldUpdateDrone() {
        Drone drone = testHelper.createNewDrone(organisation);

        browser.goTo(routes.DronesController.index().url());

        // go to edit
        browser.find("#edit-" + drone.getId()).click();

        // save it
        String newDroneName = "Super new Drone";
        int newPayload = 999;
        browser.fill(withName("name")).with(newDroneName);
        browser.fill(withName("payload")).with(String.valueOf(newPayload));

        browser.submit("#save");

        // verify
        assertThat(browser.pageSource()).contains("Delete");

        assertThat(browser.pageSource()).doesNotContain(drone.getName());
        assertThat(browser.pageSource()).contains(newDroneName);

        assertThat(browser.pageSource()).doesNotContain(String.valueOf(drone.getPayload()));
        assertThat(browser.pageSource()).contains(String.valueOf(newPayload));
    }
}