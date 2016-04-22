package dao;

import models.User;
import models.utils.AuthenticationHelper;

import java.util.List;

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
        List<User> resultList = jpaApi.em()
                .createQuery(sql, getEntityClass())
                .setParameter("email", email)
                .getResultList();

        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }


}
