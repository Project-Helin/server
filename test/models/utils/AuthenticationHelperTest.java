package models.utils;

import com.vividsolutions.jts.util.Assert;
import org.junit.Test;

public class AuthenticationHelperTest {

    @Test
    public void testPasswordEncodingAndChecking() throws Exception {
        String password = "this is a extremely secure password";
        String wrongPassword = "this is another extremely secure Password";

        String encodedPassword = AuthenticationHelper.createPassword(password);

        Assert.isTrue(AuthenticationHelper.checkPassword(password, encodedPassword));

        Assert.equals(false, AuthenticationHelper.checkPassword(wrongPassword, encodedPassword));
    }

}
