package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.TestHelper;
import models.Project;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withId;

public class ProjectsControllerTest extends AbstractIntegrationTest {

    @Inject
    private TestHelper testHelper;

    @Test
    public void shouldShowNewProject() {
        Project project = testHelper.createNewProject();

        browser.goTo(routes.ProjectsController.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(project.getName());
    }

    @Test
    public void shouldRemoveOrganisation() {
        Project project = testHelper.createNewProject();

        browser.goTo(routes.ProjectsController.index().url());
        assertThat(browser.pageSource()).contains(project.getName());

        // remove that
        browser.click(withId("delete-" + project.getId().toString()));

        waitAndClick("deleteconfirm-" + project.getId().toString());
        waitFiveSeconds();

        // verify
        browser.goTo(routes.ProjectsController.index().url());
        assertThat(browser.pageSource()).doesNotContain(project.getName());
    }

}