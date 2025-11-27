package edu.univ.erp.access;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.SettingsDAO;

public class AccessChecker {
    
    /**
     * Check if the current user is an admin
     */
    public static boolean isAdmin() {
        String role = SessionManager.getInstance().getCurrentRole();
        return "ADMIN".equalsIgnoreCase(role);
    }
    
    /**
     * Check if the current user is an instructor
     */
    public static boolean isInstructor() {
        String role = SessionManager.getInstance().getCurrentRole();
        return "INSTRUCTOR".equalsIgnoreCase(role);
    }
    
    /**
     * Check if the current user is a student
     */
    public static boolean isStudent() {
        String role = SessionManager.getInstance().getCurrentRole();
        return "STUDENT".equalsIgnoreCase(role);
    }
    
    /**
     * Check if maintenance mode is currently ON
     */
    public static boolean isMaintenanceMode() {
        return SettingsDAO.isMaintenanceMode();
    }
    
    /**
     * Check if the current user can modify data
     * Returns false if maintenance mode is ON and user is not admin
     */
    public static boolean canModify() {
        if (isAdmin()) {
            return true; // Admins can always modify
        }
        
        if (isMaintenanceMode()) {
            return false; // Non-admins cannot modify during maintenance
        }
        
        return true;
    }
    
    /**
     * Check if a user can access another user's student data
     * Students can only access their own data
     * Instructors and admins can access any student data
     */
    public static boolean canAccessStudentData(int currentUserId, int targetStudentUserId) {
        if (isAdmin() || isInstructor()) {
            return true;
        }
        
        if (isStudent()) {
            return currentUserId == targetStudentUserId;
        }
        
        return false;
    }
    
    /**
     * Check if an instructor can access a specific section
     * Instructors can only access their own sections
     */
    public static boolean canAccessInstructorSection(int instructorUserId, int sectionInstructorId) {
        if (isAdmin()) {
            return true; // Admins can access any section
        }
        
        if (isInstructor()) {
            return instructorUserId == sectionInstructorId;
        }
        
        return false;
    }
    
    /**
     * Get a standard access denied message
     */
    public static String getAccessDeniedMessage() {
        if (isMaintenanceMode() && !isAdmin()) {
            return "System is in Maintenance Mode. Changes are temporarily disabled.";
        }
        return "Access Denied: You do not have permission to perform this action.";
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }
    
    /**
     * Get current user's role
     */
    public static String getCurrentRole() {
        return SessionManager.getInstance().getCurrentRole();
    }
}