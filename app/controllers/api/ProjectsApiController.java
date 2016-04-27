package controllers.api;

import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.Route;
import ch.helin.messages.dto.way.Waypoint;
import com.google.inject.Inject;
import commons.SessionHelper;
import commons.gis.GisHelper;
import dao.ProjectsDao;
import mappers.ProjectMapper;
import models.Organisation;
import models.Project;
import models.Zone;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProjectsApiController extends Controller {

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private ProjectMapper projectMapper;

    @Transactional
    public Result index() {
        Organisation organisation = getOrganisation();

        List<Project> projects = projectsDao.findByOrganisation(organisation.getId());

        List<ProjectDto> projectDtos = projects.stream().map(projectMapper::getProjectDto).collect(Collectors.toList());
        return ok(Json.toJson(projectDtos));
    }

    private Organisation getOrganisation() {
        return sessionHelper.getOrganisation(session());
    }

    @Transactional
    public Result show(UUID projectID) {
        Project found = projectsDao.findByIdAndOrganisation(projectID, getOrganisation());
        if (found == null) {
            return forbidden("Project not found for id " + projectID.toString());
        }

        return ok(Json.toJson(projectMapper.getProjectDto(found)));
    }

    @Transactional
    public Result calculateRoute(UUID projectID, String dronePositionWkt, String customerPositionWkt) {
        Project found = projectsDao.findByIdAndOrganisation(projectID, getOrganisation());
        Position dronePosition = GisHelper.createPosition(dronePositionWkt);
        Position customerPosition = GisHelper.createPosition(customerPositionWkt);

        int wayPointCount = 20;

        Route mockRoute = createMockRoute(dronePosition, customerPosition, wayPointCount);
        return ok(Json.toJson(mockRoute));
    }

    private Route createMockRoute(Position dronePosition, Position customerPosition, int wayPointCount) {
        Route route = new Route();
        Waypoint start = new Waypoint();
        start.setPosition(dronePosition);
        start.setAction(Action.TAKEOFF);
        route.getWayPoints().add(start);

        for(int i = 1; i < wayPointCount; i++) {
            Waypoint waypoint = new Waypoint();
            waypoint.setId(UUID.randomUUID());
            double lat = customerPosition.getLat() + i * 0.0001;
            double lon = customerPosition.getLon() + i * 0.0001;
            int randomHeight = 5 + (int) (Math.random() * 100);

            waypoint.setPosition(new Position(lat, lon, randomHeight));
            route.getWayPoints().add(waypoint);
        }
        return route;
    }

    @Transactional
    public Result updateOrInsert(UUID projectId) {
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
}
