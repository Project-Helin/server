package controllers;

import com.google.inject.Inject;
import dao.OrganisationsDao;
import models.Organisation;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.organisationsUsers.index;

import java.util.Set;
import java.util.UUID;

public class OrganisationsUsersController extends Controller {

    @Inject
    private OrganisationsDao organisationsDao;

    public Result index(UUID organisationId) {
        Organisation organisation = organisationsDao.findById(organisationId);
        Set<User> administrators = organisation.getAdministrators();
        return ok(index.render(administrators));
    }

//    public Result add() {
//        Form<Organisation> form = formFactory
//                .form(Organisation.class)
//                .fill(new Organisation());
//
//        return ok(add.render(form));
//    }
//
//    public Result create() {
//        Form<Organisation> form = formFactory
//                .form(Organisation.class)
//                .bindFromRequest(request());
//
//        if (form.hasErrors()) {
//            logger.info("Has error, go back {}", form.errorsAsJson());
//            return badRequest(add.render(form));
//        } else {
//
//            Organisation organisation = form.get();
//            organisation.setId(UUID.randomUUID());
//            organisationsDao.persist(organisation);
//
//            return index();
//        }
//    }
//
//    public Result edit(UUID id) {
//        Organisation found = organisationsDao.findById(id);
//
//        if (found == null) {
//            return forbidden("Organisation not found!");
//        }
//
//        Form<Organisation> form = formFactory
//                .form(Organisation.class)
//                .fill(found);
//
//        if (form.hasErrors()) {
//            logger.info("Has error, go back {}", form.errorsAsJson());
//            return badRequest(add.render(form));
//        } else {
//            return ok(edit.render(form));
//        }
//    }
//
//    public Result update(UUID id) {
//        Organisation found = organisationsDao.findById(id);
//
//        if (found == null) {
//            return forbidden("Organisation not found!");
//        }
//
//        Form<Organisation> form = formFactory
//                .form(Organisation.class)
//                .bindFromRequest(request());
//
//        if (form.hasErrors()) {
//            logger.info("Has error, go back {}", form.errorsAsJson());
//            return badRequest(edit.render(form));
//        } else {
//
//            organisationsDao.persist(found);
//            flash("success", "Saved successfully");
//
//            return index();
//        }
//    }
//
//    public Result delete(UUID id) {
//        Organisation found = organisationsDao.findById(id);
//
//        if (found == null) {
//            return forbidden("Organisation not found!");
//        }
//
//        flash("success", "Deleted successfully");
//        organisationsDao.delete(found);
//        return index();
//    }


}
