package dao;

import com.google.inject.Inject;
import commons.AbstractE2ETest;
import models.Organisation;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.db.jpa.JPAApi;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class OrganisationsDaoTest extends AbstractE2ETest {

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private JPAApi jpaApi;

    @Test
    public void shouldSetUpdatedAndCreatedAtDate(){
        UUID savedOrganisation = jpaApi.withTransaction((em) -> {
            Organisation entity = new Organisation();
            entity.setName("HSR");
            entity.setToken(RandomStringUtils.randomAlphabetic(10));
            entity.setId(UUID.randomUUID());
            organisationsDao.persist(entity);

            return entity.getId();
        });


        // verify
        jpaApi.withTransaction((em) ->{
            Organisation found = organisationsDao.findById(savedOrganisation);
            assertThat(found.getName()).isEqualTo("HSR");
            assertThat(found.getCreatedAt()).isNotNull();
            assertThat(found.getUpdateAt()).isNotNull();

            LocalDateTime updateBefore = found.getUpdateAt();

            // update
            found.setName("HSR Rappi");
            organisationsDao.persist(found);
            em.flush();

            assertThat(found.getUpdateAt()).isNotEqualTo(updateBefore);
            return null;
        });
    }
}