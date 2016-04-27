package commons;

import dao.UserDao;
import models.Organisation;
import models.User;
import play.api.Application;
import play.api.Play;
import play.api.inject.Injector;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;

import java.util.*;

/**
 * Since Play does not allow to access the DB from template,
 * this method provides methods to do so.
 */
public class TemplateHelper {


    public static List<Organisation> getOrganisations(final String userId) {
        /**
         * This is a bit ugly, but Play has deprecated the static method JPA.withTransaction()
         * but it does not provide an alternative to get an instance of JPA.
         *
         * Here we use some static methods to get the current application,
         * get the injector from that application, and get an instance
         * of JPAApi ( the replacement to JPA )
         */
        Application currentApplication = Play.application(Play.current());
        Injector injector = currentApplication.injector();

        JPAApi jpaApi = injector.instanceOf(JPAApi.class);
        UserDao userDao = injector.instanceOf(UserDao.class);

        return jpaApi.withTransaction((em) -> {
            User user = userDao.findById(UUID.fromString(userId));

            if (user != null) {
                Set<Organisation> loadedOrganisations = user.getOrganisations();
                return new ArrayList<>(loadedOrganisations);
            }else {
                return Collections.emptyList();
            }

        });
    }


}
