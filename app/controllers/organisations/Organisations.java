package controllers.organisations;

import com.google.inject.Inject;
import dao.OrganisationsDao;
import models.Organisation;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.organisations.edit;
import views.html.organisations.index;
import views.html.organisations.show;

import java.util.List;
import java.util.UUID;


public class Organisations extends Controller {

    @Inject
    private OrganisationsDao organisationsDao;

    @Transactional(readOnly = true)
    public Result index() {
        List<Organisation> all =
                organisationsDao.findAll();


        return ok(index.render(all));
    }

    @Transactional
    public Result show(UUID id) {
        Organisation found = organisationsDao.findById(id);
        return ok(show.render(found));
    }

    @Transactional
    public Result edit(UUID id) {
        Organisation found = organisationsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        Form<Organisation> form = Form.form(Organisation.class);
        form = form.fill(found);
        return ok(edit.render(form));
    }



    public Result update(String id) {
        return play.mvc.Results.TODO;
    }

}
