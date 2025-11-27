
    

package edu.univ.erp.auth;

import edu.univ.erp.domain.User;
import java.sql.*;

public class AuthDatabase {
    
    private static final String URL = "jdbc:mysql://localhost:3306/erp_auth";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static User getUserByUsername(String username) {
        String sql = "SELECT * FROM users_auth WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users_auth WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static boolean createUser(String username, String role, String passwordHash) {
        String sql = "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, 'active')";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, passwordHash);
            
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int createUserAndGetId(String username, String role, String passwordHash) {
        String sql = "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, 'active')";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, passwordHash);
            
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public static boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = NOW() WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setRole(rs.getString("role"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setStatus(rs.getString("status"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}