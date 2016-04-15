package dao;

import models.Organisation;

public class OrganisationsDao extends AbstractDao<Organisation> {

    public OrganisationsDao() {
        super(Organisation.class);
    }

//    public List<Organisation> findByUserId(String userId) {
//        String sql =
//                "select o from Organisation o " +
//                        "join o.administrators a " +
//                        "where a.user_id = :userId"
//
//        return jpaApi.em()
//                .createQuery(sql, Organisation.class)
//                .setParameter("userId", userId)
//                .getResultList();
//    }
}
