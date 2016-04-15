package controllers;

import com.google.inject.Inject;
import commons.SessionHelper;
import commons.SessionKey;
import dao.OrganisationsDao;
import dao.UserDao;
import models.Organisation;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
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
    private UserDao userDao;

    @Inject
    private SessionHelper sessionHelper;


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

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result create() {
        Form<Organisation> form = formFactory
                .form(Organisation.class)
                .bindFromRequest(request());

        if (form.hasErrors()) {
            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(add.render(form));
        } else {
            User user = userDao.findById(UUID.fromString(session().get(SessionKey.USER_ID.name())));

            Organisation organisation = form.get();
            organisation.setId(UUID.randomUUID());
            organisation.getAdministrators().add(user);
            organisationsDao.persist(organisation);

            sessionHelper.setOrganisation(organisation, session());
            flash("success", "added Organisation");

            return redirect(routes.OrganisationsController.index());
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
