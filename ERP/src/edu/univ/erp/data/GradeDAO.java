package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    
    public static Grade getById(int gradeId) {
        String sql = "SELECT * FROM grades WHERE grade_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, gradeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractGrade(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Grade> getByEnrollment(int enrollmentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE enrollment_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                grades.add(extractGrade(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return grades;
    }
    
    public static Grade getByEnrollmentAndComponent(int enrollmentId, String component) {
        String sql = "SELECT * FROM grades WHERE enrollment_id = ? AND component = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, enrollmentId);
            ps.setString(2, component);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractGrade(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static boolean create(Grade grade) {
        String sql = "INSERT INTO grades (enrollment_id, component, score, max_score) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, grade.getEnrollmentId());
            ps.setString(2, grade.getComponent());
            ps.setDouble(3, grade.getScore());
            ps.setDouble(4, grade.getMaxScore());
            
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    grade.setGradeId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean update(Grade grade) {
        String sql = "UPDATE grades SET score = ?, max_score = ? WHERE grade_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, grade.getScore());
            ps.setDouble(2, grade.getMaxScore());
            ps.setInt(3, grade.getGradeId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateFinalGrade(int enrollmentId, String finalGrade) {
        String sql = "UPDATE grades SET final_grade = ? WHERE enrollment