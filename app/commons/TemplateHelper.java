package commons;

import dao.UserDao;
import models.Organisation;
import models.User;
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
            User user = userDao.findById(UUID.fromString(userId));
            if (user != null) {
                Set<Organisation> loadedOrganisations = user.getOrganisations();
                organisations.addAll(loadedOrganisations);
            }
        });

        if (!organisations.isEmpty()) {
            return organisations;
        } else {
            return new ArrayList<>();
        }
    }


}
