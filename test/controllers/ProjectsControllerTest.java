package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.GisHelper;
import dao.OrganisationsDao;
import dao.ProjectsDao;
import models.Organisation;
import models.Project;
import org.junit.Test;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class ProjectsControllerTest extends AbstractIntegrationTest {

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private OrganisationsDao organisationsDao;


    @Test
    public void shouldShowNewProject() {
        Project project = createNewProject();

        browser.goTo(routes.ProjectsController.index(project.getOrganisation().getId()).url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(project.getName());
    }

    @Test
    public void shouldRemoveOrganisation() {
        Project project = createNewProject();

        browser.goTo(routes.ProjectsController.index(project.getOrganisation().getId()).url());
        assertThat(browser.pageSource()).contains(project.getName());

        // remove that
        browser.click(withText("Delete"));

        // verify
        browser.goTo(routes.ProjectsController.index(project.getOrganisation().getId()).url());
        assertThat(browser.pageSource()).doesNotContain(project.getName());
    }

    private Project createNewProject() {
        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setName("Super HSR " + System.currentTimeMillis());

        Project project = new Project();
        project.setOrganisation(organisation);
        project.setId(UUID.randomUUID());
        project.setHeadquarterPosition(GisHelper.createPoint(30, 10));
        project.setName("First Demo");

        jpaapi.withTransaction(() -> {
            organisationsDao.persist(organisation);
            projectsDao.persist(project);
        });

        return project;
    }
}