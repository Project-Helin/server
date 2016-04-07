package controllers;

import com.google.inject.Inject;
import commons.AbstractIntegrationTest;
import dao.UserDao;

public class UsersControllerTest extends AbstractIntegrationTest {

    @Inject
    private UserDao userDao;


//    @Test
//    public void registerUser() {
//        User user = new User();
//        user.setName("Anna Bolika");
//        user.setEmail("anna.bolika@example.com");
//        user.setPassword("test-password");
//
//        String plainTextPassword = "test-password";
//
//        browser.goTo("/");
//
//        browser.click(withText("Register"));
//
//        assertThat(browser.pageSource()).containsIgnoringCase("Register");
//
//        fillInRegisterForm(user, plainTextPassword);
//
//        assertThat(browser.pageSource()).contains("Log in");
//    }
//
//    private void fillInRegisterForm(User user, String plainTextPassword) {
//        browser.fill(withName("name")).with(user.getName());
//        browser.fill(withName("email")).with(user.getEmail());
//        browser.fill(withName("password")).with(plainTextPassword);
//        browser.submit("Register");
//    }
//
//    @Test
//    public void login() {
//
//        User user = createUser();
//
//
//        browser.goTo(routes.ProjectsController.index(project.getOrganisation().getId()).url());
//        assertThat(browser.pageSource()).contains(project.getName());
//
//        // remove that
//        browser.click(withText("Delete"));
//
//        // verify
//        browser.goTo(routes.ProjectsController.index(project.getOrganisation().getId()).url());
//        assertThat(browser.pageSource()).doesNotContain(project.getName());
//    }
////
//    private User createUser() {
//        User user = new User();
//
//        user.setName("Anna Bolika");
//        user.setEmail("anna.bolika@example.com");
//        user.setPassword("test-password");
//
//        jpaapi.withTransaction(() -> {
//            userDao.persist(user);
//        });
//
//        return user;
//    }
}