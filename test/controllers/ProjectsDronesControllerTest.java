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
        String password = "bla";

        organisation = testHelper.createNewOrganisation();
        User user = testHelper.createUserWithOrganisation(password, organisation);


        browser.goTo("/login");
        fillInLoginForm(user, password);
    }

    @Test
    public void shouldShowDroneInProject() {
        Drone newDrone = testHelper.createNewDrone(organisation);
        newDrone.setId(UUID.randomUUID());
        Project project = testHelper.createNewProject(organisation, newDrone);

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // verify
        assertThat(browser.pageSource()).contains(newDrone.getName());
    }

    @Test
    public void shouldNotShowDroneFromOtherOrganisation() {
        Drone droneFromOtherOrganisation =
            testHelper.createNewDrone(testHelper.createNewOrganisation());
        Project project = testHelper.createNewProject(organisation);

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // verify
        assertThat(browser.pageSource()).doesNotContain(droneFromOtherOrganisation.getName());
    }

    @Test
    public void shouldAddDroneToProject() {
        Drone droneToAdd = testHelper.createNewDrone(organisation);
        droneToAdd.setId(UUID.randomUUID());

        Project project = testHelper.createNewProject(organisation);

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // add drone
        browser.click("#add");

        // verify in db
        jpaApi.withTransaction(() -> {
            Project found = projectsDao.findById(project.getId());
            assertThat(found.getDrones()).hasSize(1);

            Drone first = found.getDrones().iterator().next();
            assertThat(first.getName()).isEqualTo(droneToAdd.getName());
        });
    }

    @Test
    public void shouldDeleteDroneFromProject() {
        Drone droneToDelete = testHelper.createNewDrone(organisation);
        droneToDelete.setId(UUID.randomUUID());

        Project project = testHelper.createNewProject(organisation, droneToDelete);

        browser.goTo(routes.ProjectsController.index().url());
        browser.click("#show-drones-" + project.getId());

        // delete drone
        browser.click(withId("delete-" + droneToDelete.getId().toString()));
        waitAndClick("deleteconfirm-" + droneToDelete.getId().toString());
        waitFiveSeconds();

        // verify in db
        jpaApi.withTransaction(() -> {
            Project found = projectsDao.findById(project.getId());
            assertThat(found.getDrones()).isEmpty();
        });
    }
}