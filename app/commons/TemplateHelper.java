package commons;

import dao.UserDao;
import models.Organisation;
import play.api.Play;
import play.db.jpa.JPAApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TemplateHelper {

    public static List<Organisation> getOrganisations(final String userId) {
        JPAApi jpaApi = Play.application(Play.current()).injector().instanceOf(JPAApi.class);

        final ArrayList<Organisation> organisations = new ArrayList<>();

        jpaApi.withTransaction(() -> {
            UserDao userDao = Play.application(Play.current()).injector().instanceOf(UserDao.class);
            Set<Organisation> loadedOrganisations = userDao.findById(UUID.fromString(userId)).getOrganisations();
            organisations.addAll(loadedOrganisations);
        });

        return organisations;


    }


}
