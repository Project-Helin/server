package models;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.OrganisationsDao;
import dao.UserDao;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OrganisationUser extends AbstractIntegrationTest {

    private String plainTextPassword = "foobar";

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private UserDao userDao;

    @Test
    public void addUserToOrganisation() {
        jpaApi.withTransaction(() -> {
            User user = testHelper.createUser(plainTextPassword);

            Organisation organisation = testHelper.createNewOrganisation();
            organisation.getAdministrators().add(user);

            jpaApi.em().merge(organisation);
            jpaApi.em().flush();

            Organisation reloadedOrganisation = organisationsDao.findById(organisation.getId());
            assertThat(reloadedOrganisation.getAdministrators(), hasItem(user));
        });

        jpaApi.withTransaction(() -> {
            assertThat(organisationsDao.findAll().get(1).getAdministrators().size(), is(1));
            assertThat(userDao.findAll().get(0).getOrganisations().size(), is(1));
        });
    }

    @Test
    public void removeUserFromOrganisation() {
        jpaApi.withTransaction(() -> {
            User user = testHelper.createUser(plainTextPassword);

            Organisation organisation = testHelper.createNewOrganisation();
            organisation.getAdministrators().add(user);

            jpaApi.em().merge(organisation);
            jpaApi.em().flush();

            organisation.getAdministrators().remove(user);

            assertThat(organisation.getAdministrators().size(), is(0));

            jpaApi.em().merge(organisation);
            jpaApi.em().flush();
        });

        jpaApi.withTransaction(() -> {
            assertThat(organisationsDao.findAll().get(1).getAdministrators().size(), is(0));
            assertThat(userDao.findAll().get(0).getOrganisations().size(), is(0));
        });
    }

}
