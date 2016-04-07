package models.utils;

import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationHelper {

    public static String createPassword(String clearTextPassword) throws Exception {
        if (clearTextPassword == null) {
            throw new Exception("No password defined!");
        }
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidate, String encryptedPassword) {
        if (candidate == null || encryptedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(candidate, encryptedPassword);
    }
}
