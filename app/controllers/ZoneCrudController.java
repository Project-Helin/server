package controllers;

import models.Zone;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.zone;

import java.util.List;

public class ZoneCrudController extends Controller {

    @Transactional(readOnly = true)
    public Result index() {
        List<Zone> zones = JPA.em()
                .createQuery("select p from Zone p", Zone.class)
                .getResultList();

        return ok(zone.render(zones));
    }


}
