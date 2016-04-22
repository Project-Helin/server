package dao;

import models.Organisation;

import java.util.List;

public class OrganisationsDao extends AbstractDao<Organisation> {

    public OrganisationsDao() {
        super(Organisation.class, "organisations");
    }

    public Organisation findByOrganisationToken(String organisationToken) {
        String sql = "select o from organisations o where o.token = :organisationToken";

        List<Organisation> resultList = jpaApi.em()
                .createQuery(sql, Organisation.class)
                .setParameter("organisationToken", organisationToken)
                .getResultList();

        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}

