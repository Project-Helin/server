package controllers;

import commons.AbstractE2ETest;
import commons.drone.DroneCommunicationManager;
import controllers.messages.DroneInfosController;
import models.Drone;
import models.Organisation;
import org.junit.Before;
import org.junit.Test;
import play.*;
import play.inject.guice.GuiceApplicationBuilder;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;

public class DronesControllerTest extends AbstractE2ETest {

    private Organisation organisation;

    @Override
    protected play.Application provideApplication() {

        DroneCommunicationManager droneCommunicationManager = mock(DroneCommunicationManager.class);

        return new GuiceApplicationBuilder()
                .overrides(bind(DroneCommunicationManager.class).toInstance(droneCommunicationManager))
                .build();
    }

    @Before
    public void login() {
        organisation = doLogin();
    }

    @Test
    public void shouldShowNewDrone() {
        Drone drone = jpaApi.withTransaction(em -> testHelper.createNewDrone(organisation));

        browser.goTo(routes.DronesController.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getId().toString());
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getName());
        assertThat(browser.pageSource()).containsIgnoringCase(String.valueOf(drone.getPayload()));
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getToken().toString());
    }

    @Test
    public void shouldNotShowDroneFromAnotherOrganisation() {
        Drone drone = jpaApi.withTransaction(em -> {
            Organisation anotherOrganisation = testHelper.createNewOrganisation();
            return testHelper.createNewDrone(anotherOrganisation);
        });

        browser.goTo(routes.DronesController.index().url());

        // verify
        assertThat(browser.pageSource()).doesNotContain(drone.getId().toString());
        assertThat(browser.pageSource()).doesNotContain(drone.getName());
        assertThat(browser.pageSource()).doesNotContain(drone.getToken().toString());
    }

    @Test
    public void shouldNotAllowToRemoveDroneFromOtherOrganisation() {
        Drone drone = jpaApi.withTransaction(em -> {
            Organisation anotherOrganisation = testHelper.createNewOrganisation();
            return testHelper.createNewDrone(anotherOrganisation);
        });

        browser.goTo(routes.DronesController.delete(drone.getId()).url());

        // verify that it didn't work
        assertThat(browser.pageSource()).contains("Drone not found!");
    }

    @Test
    public void shouldUpdateDrone() {
        Drone drone = jpaApi.withTransaction(em -> testHelper.createNewDrone(organisation));

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
        assertThat(browser.pageSource()).doesNotContain(drone.getName());
        assertThat(browser.pageSource()).contains(newDroneName);

        assertThat(browser.pageSource()).contains(String.valueOf(newPayload));
    }

    @Test
    public void shouldNotAllowToUpdateDroneFromOtherOrganisation() {
        Drone drone = jpaApi.withTransaction(em -> {
            Organisation anotherOrganisation = testHelper.createNewOrganisation();
            return testHelper.createNewDrone(anotherOrganisation);
        });

        browser.goTo(routes.DronesController.edit(drone.getId()).url());

        // verify that it didn't work
        assertThat(browser.pageSource()).contains("Drone not found!");
    }
}
