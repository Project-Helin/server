package controllers.api;

import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.Route;
import ch.helin.messages.dto.way.Waypoint;
import com.google.inject.Inject;
import commons.SessionHelper;
import commons.gis.GisHelper;
import controllers.SecurityAuthenticator;
import dao.ProjectsDao;
import mappers.ProjectMapper;
import models.Organisation;
import models.Project;
import models.Zone;
import org.apache.commons.lang3.RandomUtils;
import org.geolatte.geom.PositionSequence;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

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
    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        List<Project> projects = projectsDao.findByOrganisation(getOrganisation().getId());

        List<ProjectDto> projectDtos = projects.stream().map(projectMapper::getProjectDto).collect(Collectors.toList());
        return ok(Json.toJson(projectDtos));
    }

    @Transactional
    public Result show(UUID projectID) {
        Project found = getProject(projectID);
        if (found == null) {
            return forbidden("Project not found for id " + projectID.toString());
        }

        return ok(Json.toJson(projectMapper.getProjectDto(found)));
    }

    @Transactional
    public Result calculateRoute(UUID projectID, String dronePositionWkt, String customerPositionWkt) {
        Project found = getProject(projectID);
        Position dronePosition = GisHelper.createPosition(dronePositionWkt);
        Position customerPosition = GisHelper.createPosition(customerPositionWkt);

        int wayPointCount = 20;

        Route mockRoute = createMockRoute(dronePosition, customerPosition, wayPointCount, found);

        return ok(Json.toJson(mockRoute));
    }

    private Route createMockRoute(Position dronePosition, Position customerPosition, int wayPointCount, Project found) {
        org.geolatte.geom.LineString lineString = projectsDao.calculateSkeleton(found.getId());

        Route route = new Route();
        Waypoint start = new Waypoint();
        start.setPosition(dronePosition);
        start.setAction(Action.TAKEOFF);

        PositionSequence positions = lineString.getPositions();
        for (Object each : positions) {
            org.geolatte.geom.Position p  = (org.geolatte.geom.Position) each;
            double lon = p.getCoordinate(0);
            double lat = p.getCoordinate(1);

            Waypoint waypoint = new Waypoint();
            waypoint.setId(UUID.randomUUID());
            waypoint.setPosition(new Position(lat, lon, RandomUtils.nextInt(0, 100)));
            route.getWayPoints().add(waypoint);
        }
        return route;
    }

    @Transactional
    public Result updateOrInsert(UUID projectId) {
        Project project = getProject(projectId);

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

    private Project getProject(UUID projectId) {
        return projectsDao.findByIdAndOrganisation(projectId, getOrganisation());
    }

    private Organisation getOrganisation() {
        return sessionHelper.getOrganisation(session());
    }

}
