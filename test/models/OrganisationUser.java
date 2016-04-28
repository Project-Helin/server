package models;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import dao.OrganisationsDao;
import dao.UserDao;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class OrganisationUser extends AbstractE2ETest {

    private String plainTextPassword = "foobar";

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private UserDao userDao;

    @Test
    public void addUserToOrganisation() {
        jpaApi.withTransaction(() -> {
            User user = testHelper.createUserWithOrganisation(plainTextPassword);

            Organisation organisation = testHelper.createNewOrganisation();
            organisation.getAdministrators().add(user);

            jpaApi.em().merge(organisation);
            jpaApi.em().flush();

            Organisation reloadedOrganisation = organisationsDao.findById(organisation.getId());
            assertThat(reloadedOrganisation.getAdministrators(), hasItem(user));
        });

        jpaApi.withTransaction(() -> {
            assertThat(organisationsDao.findAll().get(0).getAdministrators().size(), is(1));
            assertThat(userDao.findAll().get(0).getOrganisations().size(), is(2));
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

            jpaApi.em().merge(organisation);
            jpaApi.em().flush();
        });

        jpaApi.withTransaction(() -> {
            List<Organisation> all = organisationsDao.findAll();
            assertThat(all.size(), CoreMatchers.is(1));
            assertThat(all.get(0).getAdministrators().size(), is(0));

            List<User> users = userDao.findAll();
            assertThat(users.size(), CoreMatchers.is(1));
            assertThat(users.get(0).getOrganisations().size(), is(0));
        });
    }

}
