package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.DroneDao;
import models.Drone;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

public class DronesTest extends AbstractIntegrationTest {
    @Inject
    private DroneDao droneDao;

    @Test
    public void shouldShowNewDrone() {
        Drone drone = testHelper.createNewDrone();

        browser.goTo(routes.DronesController.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getId().toString());
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getName());
        assertThat(browser.pageSource()).containsIgnoringCase(String.valueOf(drone.getPayload()));
        assertThat(browser.pageSource()).containsIgnoringCase(drone.getToken().toString());
    }

    @Test
    public void shouldRemoveDrone() throws InterruptedException {
        Drone drone = testHelper.createNewDrone();

        browser.goTo(routes.DronesController.index().url());
        assertThat(browser.pageSource()).contains(drone.getName());
        // remove that
        // browser.find(name).click();
        waitAndClick("delete-" + drone.getId());

        //confirm delete
        waitAndClick("deleteconfirm-" + drone.getId());

        // verify
        browser.goTo(routes.DronesController.index().url());
        assertThat(browser.pageSource()).doesNotContain(drone.getName());
    }



    @Test
    public void shouldUpdateDrone() {
        Drone drone = testHelper.createNewDrone();

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


    //@Override
    //protected TestBrowser provideBrowser(int port) {
    //    return testBrowser(Helpers.FIREFOX);
    //}
}