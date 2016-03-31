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
        List<Zone> zones = (List<Zone>)

        //JPA.em().createQuery("select id, ST_asText(geom) as polygon, height FROM zone").getResultList();

        JPA.em().createQuery("select p from Zone p").getResultList();


        return ok(zone.render(zones));
    }



}
