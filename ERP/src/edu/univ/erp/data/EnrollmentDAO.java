package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    
    public static Enrollment getById(int enrollmentId) {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractEnrollment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Enrollment getByStudentAndSection(int studentId, int sectionId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND section_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractEnrollment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Enrollment> getByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                enrollments.add(extractEnrollment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return enrollments;
    }
    
    public static List<Enrollment> getBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE section_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                enrollments.add(extractEnrollment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return enrollments;
    }
    
    public static boolean create(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status, enrollment_date) " +
                    "VALUES (?, ?, ?, NOW())";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getSectionId());
            ps.setString(3, enrollment.getStatus());
            
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    enrollment.setEnrollmentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean updateStatus(int enrollmentId, String status) {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, enrollmentId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean delete(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Enrollment extractEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setSectionId(rs.getInt("section_id"));
        enrollment.setStatus(rs.getString("status"));
        enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date"));
        return enrollment;
    }
}