package controllers.api;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import commons.TestHelper;
import dao.ProjectsDao;
import dto.api.ProjectApiDto;
import dto.api.ZoneApiDto;
import models.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;

public class ProjectsApiControllerTest extends AbstractE2ETest {

    @Inject
    private ApiHelper apiHelper;

    @Inject
    private TestHelper testHelper;

    @Inject
    private ProjectsDao projectsDao;

    private Organisation currentOrganisation;

    @Before
    public void login() {
        currentOrganisation = doLogin();
    }

    @Test
    public void shouldShowProjectAsJson() {
        Project newProject = testHelper.createNewProject(
            currentOrganisation,
            testHelper.createUnsavedZone("Flight zone", ZoneType.FlightZone),
            testHelper.createUnsavedZone("Loading zone", ZoneType.LoadingZone)
        );

        ProjectApiDto project = apiHelper.doGet(
            routes.ProjectsApiController.show(newProject.getId()),
            ProjectApiDto.class,
            browser
        );

        // verify
        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo(project.getId());
        assertThat(project.getName()).isEqualTo(project.getName());

        List<ZoneApiDto> zones = sortedZone(project);
        assertThat(zones).hasSize(2);
        assertThat(zones.get(0).getName()).isEqualTo("Flight zone");
        assertThat(zones.get(0).getType()).isEqualTo(ZoneType.FlightZone);

        assertThat(zones.get(1).getName()).isEqualTo("Loading zone");
        assertThat(zones.get(1).getType()).isEqualTo(ZoneType.LoadingZone);
    }

    @Test(expected = Exception.class)
    public void shouldNotShowProjectAsJson() {
        Organisation anotherOrganisation = testHelper.createNewOrganisation();

        Project newProject = testHelper.createNewProject(
            anotherOrganisation,
            testHelper.createUnsavedZone("Flight zone", ZoneType.FlightZone),
            testHelper.createUnsavedZone("Loading zone", ZoneType.LoadingZone)
        );

        apiHelper.doPost(
            routes.ProjectsApiController.show(newProject.getId()),
            ProjectApiDto.class,
            browser
        );
    }

    @Test(expected = Exception.class)
    public void shouldShowNotExistingProject() {
        apiHelper.doGetWithJsonResponse(routes.ProjectsApiController.show(UUID.randomUUID()), ProjectApiDto.class);
    }

    @Test
    public void shouldAddNewProjectWithoutZones() {
        Project newProject = new Project();
        newProject.setName("Super Project");

        apiHelper.doPost(
            routes.ProjectsApiController.updateOrInsert(UUID.randomUUID()),
            newProject, browser);

        List<Project> found = jpaApi.withTransaction(em -> projectsDao.findAll());
        assertThat(found).hasSize(1);

        assertThat(found.get(0).getId()).isNotNull();
        assertThat(found.get(0).getName()).isEqualTo(newProject.getName());
    }

    @Test
    public void shouldUpdateProjectWithoutZones() {
        Project project = testHelper.createNewProject(currentOrganisation);

        ProjectApiDto projectDto = new ProjectApiDto(project.getId(), "My Super Project", Collections.emptyList());
        apiHelper.doPost(routes.ProjectsApiController.updateOrInsert(projectDto.getId()), projectDto, browser);

        List<Project> found = jpaApi.withTransaction(em -> projectsDao.findAll());
        assertThat(found).hasSize(1);

        assertThat(found.get(0).getId()).isNotNull();
        assertThat(found.get(0).getName()).isEqualTo("My Super Project");
    }

    @Test
    public void shouldAddProjectWithZones() {
        ProjectApiDto projectDto = new ProjectApiDto();
        projectDto.setId(UUID.randomUUID());
        projectDto.setName("My Super Project");
        projectDto.setZones(Collections.singletonList(
            new ZoneApiDto(UUID.randomUUID(), null, 10, ZoneType.LoadingZone, "My Flightzone")
        ));

        apiHelper.doPost(routes.ProjectsApiController.updateOrInsert(projectDto.getId()), projectDto, browser);

        jpaApi.withTransaction(() -> {
            List<Project> found = projectsDao.findAll();
            assertThat(found).hasSize(1);

            // verify zones
            assertThat(found.get(0).getZones()).hasSize(1);
            Zone firstZone = found.get(0).getZones().iterator().next();
            assertThat(firstZone.getName()).isEqualTo("My Flightzone");
            assertThat(firstZone.getType()).isEqualTo(ZoneType.LoadingZone);
        });
    }

    @Test
    public void shouldRemoveZoneFromProject() {
        Project newProject = testHelper.createNewProject(
            currentOrganisation,
            testHelper.createUnsavedZone("Flight zone", ZoneType.FlightZone),
            testHelper.createUnsavedZone("Loading zone", ZoneType.LoadingZone)
        );

        Zone firstZone = newProject.getZones().iterator().next();
        ProjectApiDto projectDto = mapToDto(newProject, firstZone);

        apiHelper.doPost(routes.ProjectsApiController.updateOrInsert(projectDto.getId()), projectDto, browser);

        jpaApi.withTransaction(() -> {
            List<Project> found = projectsDao.findAll();
            assertThat(found).hasSize(1);

            // should only be one there
            assertThat(found.get(0).getZones()).hasSize(1);

            Zone savedZone = found.get(0).getZones().iterator().next();
            assertThat(savedZone.getId()).isEqualTo(firstZone.getId());
            assertThat(savedZone.getName()).isEqualTo(firstZone.getName());
            assertThat(savedZone.getType()).isEqualTo(firstZone.getType());
        });
    }


    @Test
    public void shouldAddZoneToProject() {
        Zone firstSavedZone = testHelper.createUnsavedZone("Flight zone", ZoneType.FlightZone);
        Project newProject = testHelper.createNewProject(currentOrganisation, firstSavedZone);

        Zone firstZone = newProject.getZones().iterator().next();
        ZoneApiDto secondNew = new ZoneApiDto(UUID.randomUUID(), null, 100, ZoneType.LoadingZone, "JO");

        ProjectApiDto projectDto = mapToDto(newProject, firstZone);
        projectDto.setZones(Arrays.asList(zoneToDto(firstZone), secondNew));

        apiHelper.doPost(routes.ProjectsApiController.updateOrInsert(projectDto.getId()), projectDto, browser);

        jpaApi.withTransaction(() -> {
            List<Project> found = projectsDao.findAll();
            assertThat(found).hasSize(1);

            Set<Zone> zones = found.get(0).getZones();
            List<Zone> sortedZones = sort(zones);

            assertThat(sortedZones).hasSize(2);

            Zone first = sortedZones.get(0);
            assertThat(first.getId()).isEqualTo(firstZone.getId());
            assertThat(first.getName()).isEqualTo(firstZone.getName());
            assertThat(first.getType()).isEqualTo(firstZone.getType());

            Zone second = sortedZones.get(1);
            assertThat(second.getId()).isEqualTo(secondNew.getId());
            assertThat(second.getName()).isEqualTo(secondNew.getName());
            assertThat(second.getType()).isEqualTo(secondNew.getType());
        });
    }

    @Test
    public void shouldUpdateSavedZones() {
        Zone firstSavedZone = testHelper.createUnsavedZone("Flight zone", ZoneType.FlightZone);
        Project newProject = testHelper.createNewProject(currentOrganisation, firstSavedZone);

        Zone firstZone = newProject.getZones().iterator().next();
        firstZone.setName("Jo!");

        ProjectApiDto projectDto = mapToDto(newProject, firstZone);
        apiHelper.doPost(routes.ProjectsApiController.updateOrInsert(projectDto.getId()), projectDto, browser);

        jpaApi.withTransaction(() -> {
            List<Project> found = projectsDao.findAll();
            assertThat(found).hasSize(1);

            Set<Zone> zones = found.get(0).getZones();
            List<Zone> sortedZones = sort(zones);

            assertThat(sortedZones).hasSize(1);

            Zone first = sortedZones.get(0);
            assertThat(first.getId()).isEqualTo(firstZone.getId());
            assertThat(first.getType()).isEqualTo(firstZone.getType());
            assertThat(first.getName()).isEqualTo("Jo!");
        });
    }

    private ProjectApiDto mapToDto(Project newProject, Zone... zones) {
        ProjectApiDto projectDto = new ProjectApiDto();
        projectDto.setId(newProject.getId());
        projectDto.setName(newProject.getName());

        List<ZoneApiDto> collect = Arrays
            .stream(zones)
            .map(this::zoneToDto)
            .collect(Collectors.toList());

        projectDto.setZones(collect);
        return projectDto;
    }

    private ZoneApiDto zoneToDto(Zone firstZone) {
        return new ZoneApiDto(
            firstZone.getId(),
            firstZone.getPolygon(),
            firstZone.getHeight(),
            firstZone.getType(),
            firstZone.getName()
        );
    }

    private List<Zone> sort(Set<Zone> zones) {
        return zones
            .stream()
            .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
            .collect(Collectors.toList());
    }

    private List<ZoneApiDto> sortedZone(ProjectApiDto project) {
        return project.getZones()
            .stream()
            .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
            .collect(Collectors.toList());
    }
}