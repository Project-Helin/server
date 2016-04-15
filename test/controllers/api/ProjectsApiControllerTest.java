package controllers.api;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.ProjectsDao;
import models.Project;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;

public class ProjectsApiControllerTest extends AbstractIntegrationTest{

    @Inject
    private ApiHelper apiHelper;

    @Inject
    private ProjectsDao projectsDao;

    @Test
    public void shouldShowProjectAsJson(){
        /*
        Project newProject = testHelper.createNewProject(
            testHelper.createUnsavedZone("Flight zone", ZoneType.FlightZone),
            testHelper.createUnsavedZone("Loading zone", ZoneType.LoadingZone)
        );

        ProjectDto project = apiHelper.doGetWithJsonResponse(routes.ProjectsApiController.show(newProject.getId()),
            ProjectDto.class);

        // verify
        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo(project.getId());
        assertThat(project.getName()).isEqualTo(project.getName());

        List<ZoneDto> zones = sortedZone(project);
        assertThat(zones).hasSize(2);
        assertThat(zones.get(0).getName()).isEqualTo("Flight zone");
        assertThat(zones.get(0).getType()).isEqualTo(ZoneType.FlightZone);

        assertThat(zones.get(1).getName()).isEqualTo("Loading zone");
        assertThat(zones.get(1).getType()).isEqualTo(ZoneType.LoadingZone);
        */
    }

    @Test(expected = Exception.class)
    public void shouldShowNotExistingProject() {
        apiHelper.doGetWithJsonResponse(routes.ProjectsApiController.show(UUID.randomUUID()), ProjectDto.class);
    }

    @Test
    public void shouldAddNewProjectWithoutZones() {
        Project newProject = new Project();
        newProject.setName("Super Project");

        apiHelper.doPost(routes.ProjectsApiController.updateOrInsert(UUID.randomUUID()), newProject);

        List<Project> found = jpaApi.withTransaction(em -> projectsDao.findAll());
        assertThat(found).hasSize(1);

        assertThat(found.get(0).getId()).isNotNull();
        assertThat(found.get(0).getName()).isEqualTo(newProject.getName());
    }

    private List<ZoneDto> sortedZone(ProjectDto project) {
        return project.getZones()
            .stream()
            .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
            .collect(Collectors.toList());
    }
}