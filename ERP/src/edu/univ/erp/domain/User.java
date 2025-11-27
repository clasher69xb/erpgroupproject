

package edu.univ.erp.domain;

import java.time.LocalDateTime;
import java.util.Objects;


public class User {
    private int userId;
    private String username;
    private String role; // STUDENT, INSTRUCTOR, ADMIN
    private String status; // ACTIVE, INACTIVE, LOCKED
    private LocalDateTime lastLogin;
    
    public User() {}
    
    public User(int userId, String username, String role, String status) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.status = status;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", username='" + username + 
               "', role='" + role + "', status='" + status + "'}";
    }
}