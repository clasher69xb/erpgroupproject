package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {
    
    public static Instructor getByUserId(int userId) {
        String sql = "SELECT * FROM instructors WHERE user_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractInstructor(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Instructor> getAll() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT * FROM instructors ORDER BY name";
        
        try (Connection conn = ERPDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                instructors.add(extractInstructor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return instructors;
    }
    
    public static boolean create(Instructor instructor) {
        String sql = "INSERT INTO instructors (user_id, name, department, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, instructor.getUserId());
            ps.setString(2, instructor.getName());
            ps.setString(3, instructor.getDepartment());
            ps.setString(4, instructor.getEmail());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean update(Instructor instructor) {
        String sql = "UPDATE instructors SET name = ?, department = ?, email = ? WHERE user_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, instructor.getName());
            ps.setString(2, instructor.getDepartment());
            ps.setString(3, instructor.getEmail());
            ps.setInt(4, instructor.getUserId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Instructor extractInstructor(ResultSet rs) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setUserId(rs.getInt("user_id"));
        instructor.setName(rs.getString("name"));
        instructor.setDepartment(rs.getString("department"));
        instructor.setEmail(rs.getString("email"));
        return instructor;
    }
}