package controllers;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.ProjectsDao;
import models.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withId;

public class ProjectsDronesControllerTest extends AbstractE2ETest {

    @Inject
    private ProjectsDao projectsDao;

    private Organisation organisation;

    @Before
    public void login() {
        organisation = doLogin();
    }

    @Test
    public void shouldShowDroneInProject() {
        final Drone[] newDrone = new Drone[1];

        Project project = jpaApi.withTransaction(em -> {
            newDrone[0] = testHelper.createNewDrone(organisation);
            return testHelper.createNewProject(organisation, newDrone[0]);
        });

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // verify
        assertThat(browser.pageSource()).contains(newDrone[0].getName());
    }

    @Test
    public void shouldNotShowDroneFromOtherOrganisation() {
        Drone droneFromOtherOrganisation =
            jpaApi.withTransaction(em -> testHelper.createNewDrone(testHelper.createNewOrganisation()));
        Project project = jpaApi.withTransaction(em -> testHelper.createNewProject(organisation));

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // verify
        assertThat(browser.pageSource()).doesNotContain(droneFromOtherOrganisation.getName());
    }

    @Test
    public void shouldAddDroneToProject() {
        final Drone[] droneToAdd = new Drone[1];

        Project project = jpaApi.withTransaction(em -> {
            droneToAdd[0] = testHelper.createNewDrone(organisation);
            return testHelper.createNewProject(organisation);
        });

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // add drone
        browser.click("#add");

        // verify in db
        jpaApi.withTransaction(() -> {
            Project found = projectsDao.findById(project.getId());
            assertThat(found.getDrones()).hasSize(1);

            Drone firstDrone = found.getDrones().iterator().next();
            assertThat(firstDrone.getName()).isEqualTo(droneToAdd[0].getName());
            assertThat(firstDrone.getProject()).isEqualTo(found);
        });
    }

    @Test
    public void shouldDeleteDroneFromProject() {
        final Drone[] droneToDelete = new Drone[1];

        Project project = jpaApi.withTransaction(em -> {
            droneToDelete[0] = testHelper.createNewDrone(organisation);
            return testHelper.createNewProject(organisation, droneToDelete[0]);
        });

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // delete drone
        browser.click(withId("delete-" + droneToDelete[0].getId().toString()));
        waitAndClick("deleteconfirm-" + droneToDelete[0].getId().toString());
        waitFiveSeconds();

        // verify in db
        jpaApi.withTransaction(() -> {
            Project found = projectsDao.findById(project.getId());
            assertThat(found.getDrones()).isEmpty();
        });
    }
}