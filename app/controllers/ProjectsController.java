package controllers;

import com.google.inject.Inject;
import dao.OrganisationsDao;
import dao.ProjectsDao;
import models.Organisation;
import models.Project;
import models.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.projects.edit;
import views.html.projects.index;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
public class ProjectsController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private OrganisationsDao organisationsDao;

    public Result index() {
        UUID organisationId = getOrganisation().getId();

        logger.info("Organisation id {}", organisationId);
        List<Project> all = projectsDao.findByOrganisation(organisationId);

        return ok(index.render(all));
    }

    public Result add() {
        UUID newProjectId = UUID.randomUUID();
        return ok(edit.render(newProjectId));
    }

    public Result create() {
        UUID id = UUID.randomUUID();
        return ok(edit.render(id));
    }

    public Result edit(UUID id) {
        return ok(edit.render(id));
    }


    /**
     * @return Project as json
     */
    public Result show(UUID projectID) {
        Project found = projectsDao.findById(projectID);
        if (found == null) {
            return forbidden("Organisation not found!");
        }

        List<ZoneDto> zones = new ArrayList<>();
        for (Zone zone : found.getZones()) {
            zones.add(new ZoneDto(zone.getId(), zone.getPolygon(), zone.getHeight(), zone.getType(), zone.getName()));
        }

        ProjectDto projectDto = new ProjectDto(
                found.getId(),
                found.getName(),
                zones
        );

        return ok(Json.toJson(projectDto));
    }

    public Result update(UUID projectId) {
        Project project = projectsDao.findById(projectId);

        boolean isNewProject = project == null;
        if (isNewProject) {
            // create new project
            project = new Project();
            project.setId(UUID.randomUUID());
            project.setOrganisation(getOrganisation());
        }

        ProjectDto fromRequest =
                Json.fromJson(request().body().asJson(), ProjectDto.class);
        // set all fields
        project.setName(fromRequest.getName());
        addZonesToProject(project, fromRequest);
        projectsDao.persist(project);

        return ok();
    }

    private Organisation getOrganisation() {

        /**
         * For now -> HSR is always there
         */
        return organisationsDao
                .findAll()
                .stream()
                .filter(new Predicate<Organisation>() {
                    @Override
                    public boolean test(Organisation organisation) {
                        return organisation.getName().equals("HSR");
                    }
                })
                .collect(Collectors.toList()).get(0);
    }

    /**
     * This needs a special handling - because we need to check if
     * there are already saved sones, or new ones.
     */
    private void addZonesToProject(Project project,
                                   ProjectDto fromRequest) {

        Set<Zone> zones = project.getZones();
        HashSet<Zone> previousZones = new HashSet<>(zones);

        /**
         * we cannot set a new HashSet because, jpa need to track changes
         */
        project.getZones().clear();
        project.getZones().addAll(mapAll(fromRequest, project, previousZones));
    }

    private List<Zone> mapAll(ProjectDto fromRequest,
                              Project project,
                              Set<Zone> previousZones) {

        return fromRequest
                .getZones()
                .stream()
                .map(zoneDto -> {
                    Zone zone = new Zone();
                    zone.setId(zoneDto.getId());

                    if(previousZones.contains(zone)){
                        zone = findById(previousZones, zoneDto);
                    }

                    zone.setType(zoneDto.getType());
                    zone.setHeight(zoneDto.getHeight());
                    zone.setPolygon(zoneDto.getPolygon());
                    zone.setName(zoneDto.getName());
                    zone.setProject(project);
                    return zone;
                }).collect(Collectors.toList());
    }

    private Zone findById(Set<Zone> previousZones, ZoneDto zoneDto) {
        return previousZones.stream().filter(o -> o.getId().equals(zoneDto.getId())).findFirst().get();
    }

    public Result delete(UUID projectID) {
        Project found = projectsDao.findById(projectID);

        if (found == null) {
            return forbidden("Organisation not found!");

        }

        projectsDao.delete(found);
        return index();
    }

}
