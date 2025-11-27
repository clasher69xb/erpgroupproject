
    
package edu.univ.erp.auth;

import edu.univ.erp.domain.User;

public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public String getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }
    
    public void logout() {
        currentUser = null;
    }
}