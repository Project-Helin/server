package controllers;

import com.google.inject.Inject;
import dao.OrganisationsDao;
import models.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.organisations.add;
import views.html.organisations.edit;
import views.html.organisations.index;

import java.util.List;
import java.util.UUID;

@Transactional
public class OrganisationsController extends Controller {

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private FormFactory formFactory;

    private static final Logger logger = LoggerFactory.getLogger(OrganisationsController.class);

    public Result index() {
        List<Organisation> all =
                organisationsDao.findAll();
        return ok(index.render(all));
    }

    public Result add() {
        Form<Organisation> form = formFactory
                .form(Organisation.class)
                .fill(new Organisation());

        return ok(add.render(form));
    }

    public Result create() {
        Form<Organisation> form = formFactory
                .form(Organisation.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(add.render(form));
        } else {

            Organisation organisation = form.get();
            organisation.setId(UUID.randomUUID());
            organisationsDao.persist(organisation);

            return index();
        }
    }

    public Result edit(UUID id) {
        Organisation found = organisationsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        Form<Organisation> form = formFactory
                .form(Organisation.class)
                .fill(found);

        if (form.hasErrors()) {
            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(add.render(form));
        } else {
            return ok(edit.render(form));
        }
    }

    public Result update(UUID id) {
        Organisation found = organisationsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        Form<Organisation> form = formFactory
                .form(Organisation.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(edit.render(form));
        } else {

            organisationsDao.persist(found);
            flash("success", "Saved successfully");

            return index();
        }
    }

    public Result delete(UUID id) {
        Organisation found = organisationsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        flash("success", "Deleted successfully");
        organisationsDao.delete(found);
        return index();
    }
}
