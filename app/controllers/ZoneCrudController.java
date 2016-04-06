package controllers;

import models.Zone;
import org.geolatte.geom.Point;
import org.geolatte.geom.*;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsParameter;
import org.geolatte.geom.crs.CrsRegistry;
import org.h2.command.dml.Query;
import org.h2.engine.Session;
import org.h2.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.zone;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.geolatte.geom.codec.Wkt.fromWkt;
import static play.libs.Json.toJson;

public class ZoneCrudController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(ZoneCrudController.class);

    @Transactional(readOnly = true)
    public Result index() {
        List<Zone> zones = (List<Zone>)

        //JPA.em().createQuery("select id, ST_asText(geom) as polygon, height FROM zone").getResultList();

        JPA.em().createQuery("select p from Zone p").getResultList();


        //return ok(toJson(zones));
        return ok(zone.render(zones));
    }

    @Transactional(readOnly = true)
    public Result calcRoute() {
        Point p1 = (Point) Wkt.fromWkt("SRID=4326; POINT(8.815412 47.223812)");
        Point p2 = (Point) Wkt.fromWkt("SRID=4326; POINT(8.816458 47.223562)");


//        List resultList = JPA.em().createNativeQuery("SELECT z.id as id, z.geom as geom, z.height as height FROM zone z " +
//                " WHERE ST_contains(z.geom, ST_setSRID(ST_Point(8.815412,47.223812), 4326) ) = true " +
//                " AND ST_contains(z.geom, ST_setSRID(ST_Point(8.816458,47.223562), 4326) ) = true", Zone.class).getResultList();

        TypedQuery<Zone> query = JPA.em().createQuery("SELECT z FROM Zone z " +
                " WHERE ST_contains(z.geom, :p1 ) = true " +
                " AND ST_contains(z.geom, :p2  ) = true", Zone.class);
        query.setParameter("p1", p1);
        query.setParameter("p2", p2);
        List<Zone> resultList = query.getResultList();

        System.out.println("=> Got result, print all results");
        for (Zone o : resultList) {
            System.out.println(o.toString());
        }
        return ok(toJson(resultList));
    }

 }
