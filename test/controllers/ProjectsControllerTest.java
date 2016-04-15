package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import commons.TestHelper;
import dao.ProjectsDao;
import models.Project;
import models.User;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withId;

public class ProjectsControllerTest extends AbstractIntegrationTest {

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private TestHelper testHelper;

    User user;

    @Before
    public void login() {
        String password = "bla";
        user = testHelper.createUserWithOrganisation(password);
        browser.goTo("/login");
        fillInLoginForm(user, password);
    }

    @Test
    public void shouldShowNewProject() {
        Project project = testHelper.createNewProject(user);

        browser.goTo(routes.ProjectsController.index().url());

        // verify
        assertThat(browser.pageSource()).containsIgnoringCase(project.getName());
    }

    @Test
    public void shouldRemoveProject() {
        Project project = testHelper.createNewProject(user);

        browser.goTo(routes.ProjectsController.index().url());
        assertThat(browser.pageSource()).contains(project.getName());

        // remove that
        browser.click(withId("delete-" + project.getId().toString()));

        waitAndClick("deleteconfirm-" + project.getId().toString());
        waitFiveSeconds();

        // verify
        browser.goTo(routes.ProjectsController.index().url());
        assertThat(browser.pageSource()).doesNotContain(project.getName());

        // verify in db
        jpaApi.withTransaction(() -> {
            assertThat(projectsDao.findById(project.getId())).isNull();
        });
    }

}