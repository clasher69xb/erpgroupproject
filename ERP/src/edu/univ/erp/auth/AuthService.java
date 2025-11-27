
package edu.univ.erp.auth;

import edu.univ.erp.domain.User;

public class AuthService {
    
    public static User login(String username, String password) {
        User user = AuthDatabase.getUserByUsername(username);
        
        if (user == null) {
            return null;
        }
        
        if (!user.getStatus().equals("active")) {
            return null;
        }
        
        if (PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            AuthDatabase.updateLastLogin(user.getUserId());
            SessionManager.getInstance().setCurrentUser(user);
            return user;
        }
        
        return null;
    }
    
    public static void logout() {
        SessionManager.getInstance().logout();
    }
    
    public static boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = AuthDatabase.getUserById(userId);
        
        if (user == null) {
            return false;
        }
        
        if (!PasswordHasher.verifyPassword(oldPassword, user.getPasswordHash())) {
            return false;
        }
        
        String newHash = PasswordHasher.hashPassword(newPassword);
        return AuthDatabase.updatePassword(userId, newHash);
    }
    
    public static User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }
    
    public static boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }
}