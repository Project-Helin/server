package dao;

import models.User;
import models.utils.AuthenticationHelper;

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

    public User findByEmail (String email) {
        String sql = "select e from users e where e.email=:email";

        TypedQuery<User> email1 = jpaApi.em()
            .createQuery(sql, getEntityClass())
            .setParameter("email", email);

        return getSingleResultOrNull(email1);
    }
}
