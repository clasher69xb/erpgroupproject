
package edu.univ.erp.data;

import java.sql.*;

public class SettingsDAO {
    
    /**
     * Check if maintenance mode is currently enabled
     */
    public static boolean isMaintenanceMode() {
        String sql = "SELECT value FROM settings WHERE key = 'maintenance_mode'";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String value = rs.getString("value");
                return "true".equalsIgnoreCase(value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false; // Default to false if not found
    }
    
    /**
     * Set maintenance mode ON or OFF
     */
    public static boolean setMaintenanceMode(boolean enabled) {
        String sql = "UPDATE settings SET value = ? WHERE key = 'maintenance_mode'";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, enabled ? "true" : "false");
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a setting value by key
     */
    public static String getSetting(String key) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Set a setting value
     */
    public static boolean setSetting(String key, String value) {
        String sql = "INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?)";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, key);
            ps.setString(2, value);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get current semester
     */
    public static String getCurrentSemester() {
        return getSetting("current_semester");
    }
    
    /**
     * Get current year
     */
    public static int getCurrentYear() {
        String year = getSetting("current_year");
        try {
            return year != null ? Integer.parseInt(year) : 2024;
        } catch (NumberFormatException e) {
            return 2024;
        }
    }
    
    /**
     * Set current semester and year
     */
    public static boolean setCurrentSemesterYear(String semester, int year) {
        boolean s1 = setSetting("current_semester", semester);
        boolean s2 = setSetting("current_year", String.valueOf(year));
        return s1 && s2;
    }
}