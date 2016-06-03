package dao;

import models.User;
import models.utils.AuthenticationHelper;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class UserDao extends AbstractDao<User> {

    public UserDao() {
        super(User.class, "users");
    }

    public User authenticateAndGetUser(String email, String password) {
        User user = findByEmail(email);
        if (user != null && AuthenticationHelper.checkPassword(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    public User findByEmail(String email) {
        String sql = "select e from users e where e.email=:email";

        TypedQuery<User> query = jpaApi.em()
            .createQuery(sql, getEntityClass())
            .setParameter("email", email);

        return getSingleResultOrNull(query);
    }

    public boolean isEmailAvailable(String email) {
        String sql = "select count(e) from users e where e.email=:email";

        Query query = jpaApi.em()
            .createQuery(sql)
            .setParameter("email", email);

        Long count = (Long) query.getSingleResult();
        return count == 0;
    }
}
