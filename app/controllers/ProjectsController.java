package controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.inject.Inject;
import commons.JsonPointDeserializer;
import commons.JsonPointSerializer;
import dao.ProjectsDao;
import models.Organisation;
import models.Project;
import org.geolatte.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.projects.edit;
import views.html.projects.index;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
public class ProjectsController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private FormFactory formFactory;

    public Result index() {
        /**
         * TODO Kiru: this should from from the current user
         */
        UUID organisationId = UUID.fromString("556963dd-94a3-46b0-9576-a27cb39fe62b");
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
        /**
         * This is an angular page - so not that much to do here.
         */
        return ok(edit.render(id));
    }

    public Result edit(UUID id) {
        /**
         * This is an angular page - so not that much to do here.
         */
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

        ProjectDto projectDto = new ProjectDto(
                found.getId(),
                found.getName(),
                found.getHeadquarterPosition()
        );

        return ok(Json.toJson(projectDto));
    }

    public Result update(UUID organisationId, UUID id) {
        Organisation found = null; // organisationsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }
//        organisationsDao.persist(found);

        return index();
    }

    public Result delete(UUID projectID) {
        Project found = projectsDao.findById(projectID);

        if (found == null) {
            return forbidden("Organisation not found!");

        }

        projectsDao.delete(found);
        return index();
    }

    public static class ProjectDto {
        public final UUID id;
        public final String name;

        @JsonSerialize(using = JsonPointSerializer.class)
        @JsonDeserialize(using = JsonPointDeserializer.class)
        public final Point headquarterPosition;


        public ProjectDto(UUID id, String name, Point headquarterPosition) {
            this.id = id;
            this.name = name;
            this.headquarterPosition = headquarterPosition;
        }
    }
}
