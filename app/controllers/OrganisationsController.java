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

    public final int ORGANISATION_TOKEN_LENGTH = 5;

    private static final Logger logger = LoggerFactory.getLogger(OrganisationsController.class);

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
            organisation.setToken(UUID.randomUUID().toString().substring(0, ORGANISATION_TOKEN_LENGTH));
            organisationsDao.persist(organisation);

            sessionHelper.setOrganisation(organisation, session());
            flash("success", "added Organisation");

            return redirect(routes.Application.index());
        }
    }

    public Result edit() {
        Organisation found = sessionHelper.getOrganisation(session());

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
            sessionHelper.setOrganisation(found, session());
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

            return redirect(routes.Application.index());
        }
    }
}
