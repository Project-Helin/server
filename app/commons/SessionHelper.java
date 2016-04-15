package commons;

import ch.helin.messages.commons.AssertUtils;
import com.google.inject.Inject;
import dao.OrganisationsDao;
import dao.UserDao;
import models.Organisation;
import models.User;
import play.mvc.Http;

import java.util.UUID;

public class SessionHelper {

    @Inject
    private UserDao userDao;

    @Inject
    private OrganisationsDao organisationsDao;

    public User getUser(Http.Session session) {
        String userId = session.get(SessionKey.USER_ID.name());

        return userDao.findById(UUID.fromString(userId));
    }

    public void setUser(User user, Http.Session session) {
        session.put(SessionKey.NAME.name(), user.getName());
        session.put(SessionKey.USER_ID.name(), user.getId().toString());
    }


    public Organisation getOrganisation(Http.Session session) {
        String organisationId = session.get(SessionKey.ORGANISATION_ID.name());
        AssertUtils.throwExceptionIfNull(organisationId, "No organisations id found in session");

        return organisationsDao.findById(UUID.fromString(organisationId));
    }


    public void setOrganisation(Organisation organisation, Http.Session session) {
        session.put(SessionKey.ORGANISATION_ID.name(), organisation.getId().toString());
        session.put(SessionKey.ORGANISATION_NAME.name(), organisation.getName());
    }
}
