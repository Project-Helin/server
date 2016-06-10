package controllers;

import com.google.inject.Inject;
import service.SessionHelper;
import dao.OrganisationsDao;
import dao.UserDao;
import models.Organisation;
import models.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.organisationsUsers.add;
import views.html.organisationsUsers.index;

import java.util.Set;
import java.util.UUID;

@Transactional
public class OrganisationsUsersController extends Controller {

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private UserDao userDao;

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    FormFactory formFactory;

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        Organisation organisation = sessionHelper.getOrganisation(session());
        Set<User> administrators = organisation.getAdministrators();
        return ok(index.render(administrators, organisation));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result add() {
        Organisation organisation = sessionHelper.getOrganisation(session());
        return ok(add.render(organisation));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result create() {
        DynamicForm requestData = formFactory.form().bindFromRequest();
        String email = requestData.get("email");

        User user = userDao.findByEmail(email);
        Organisation organisation = sessionHelper.getOrganisation(session());

        if (user == null) {
            flash("error", "User does not exist");
            return redirect(routes.OrganisationsUsersController.add());
        } else {
            organisation.getAdministrators().add(user);
            organisationsDao.persist(organisation);

            flash("success", "Added " + user.getName() + " as Administrator");
            return redirect(routes.OrganisationsUsersController.index());
        }
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result delete(UUID userId) {
        Organisation organisation = sessionHelper.getOrganisation(session());
        User user = userDao.findById(userId);

        if (user == null || organisation == null) {
            flash("error", "User does not exist");
            return redirect(routes.OrganisationsUsersController.index());
        } else if (!organisation.getAdministrators().contains(user)) {
            flash("error", "User is not registered as an administrator of this organisation");
            return redirect(routes.OrganisationsUsersController.index());
        } else if (organisation.getAdministrators().size() == 1 ) {
            flash("error", "You can't delete the last administrator of an organisation");
            return redirect(routes.OrganisationsUsersController.index());
        } else {
            organisation.getAdministrators().remove(user);
            flash("success", "Deleted successfully");
            return redirect(routes.OrganisationsUsersController.index());
        }
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result setCurrentOrganisation (UUID organisationId) {
        User user = sessionHelper.getUser(session());
        Organisation organisation = organisationsDao.findById(organisationId);

        if (user.getOrganisations().contains(organisation)) {
            flash("success", "Changed Organisation to " + organisation.getName());
            sessionHelper.setOrganisation(organisation, session());
            return redirect(request().getHeader("referer"));
        } else {
            flash("error", "You are not an administrator of this organisation");
            return redirect(routes.Application.index());
        }


    }


}
