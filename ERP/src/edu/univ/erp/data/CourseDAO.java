package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    
    public static Course getById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractCourse(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Course getByCode(String code) {
        String sql = "SELECT * FROM courses WHERE code = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractCourse(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Course> getAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY code";
        
        try (Connection conn = ERPDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(extractCourse(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courses;
    }
    
    public static boolean create(Course course) {
        String sql = "INSERT INTO courses (code, title, credits, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getDescription());
            
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    course.setCourseId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean update(Course course) {
        String sql = "UPDATE courses SET code = ?, title = ?, credits = ?, description = ? WHERE course_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getDescription());
            ps.setInt(5, course.getCourseId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Course extractCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCode(rs.getString("code"));
        course.setTitle(rs.getString("title"));
        course.setCredits(rs.getInt("credits"));
        course.setDescription(rs.getString("description"));
        return course;
    }
}