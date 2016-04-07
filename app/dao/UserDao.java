package dao;

import models.User;
import models.utils.AuthenticationHelper;

public class UserDao extends AbstractDao<User> {

    public UserDao() {
        super(User.class);
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
        String sql = "select e from " + getEntityClass().getSimpleName() + " e where e.email=" + email;
        return jpaApi.em()
                .createQuery(sql, getEntityClass())
                .getSingleResult();
    }


}
