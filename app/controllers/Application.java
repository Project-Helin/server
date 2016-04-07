package controllers;

import models.Organisation;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public Result index() {
        return ok(index.render());
    }

    @Transactional
    public Result addPerson() {
        Organisation person = Form.form(Organisation.class).bindFromRequest().get();
        if (person.getName() == null) {
            throw new NullPointerException("Name is empty");
        }
        JPA.em().persist(person);
        return redirect(routes.Application.index());
    }

    @Transactional(readOnly = true)
    public Result getPersons() {
        List<Organisation> persons = (List<Organisation>)
            JPA.em().createQuery("select p from Organisation p").getResultList();
        return ok(toJson(persons));
    }
}
