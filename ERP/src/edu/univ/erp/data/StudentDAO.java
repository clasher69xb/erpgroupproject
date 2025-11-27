package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    public static Student getByUserId(int userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Student getByRollNo(String rollNo) {
        String sql = "SELECT * FROM students WHERE roll_no = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY roll_no";
        
        try (Connection conn = ERPDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(extractStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    public static boolean create(Student student) {
        String sql = "INSERT INTO students (user_id, roll_no, name, program, year, email) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, student.getUserId());
            ps.setString(2, student.getRollNo());
            ps.setString(3, student.getName());
            ps.setString(4, student.getProgram());
            ps.setInt(5, student.getYear());
            ps.setString(6, student.getEmail());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean update(Student student) {
        String sql = "UPDATE students SET name = ?, program = ?, year = ?, email = ? " +
                    "WHERE user_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, student.getName());
            ps.setString(2, student.getProgram());
            ps.setInt(3, student.getYear());
            ps.setString(4, student.getEmail());
            ps.setInt(5, student.getUserId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Student extractStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setUserId(rs.getInt("user_id"));
        student.setRollNo(rs.getString("roll_no"));
        student.setName(rs.getString("name"));
        student.setProgram(rs.getString("program"));
        student.setYear(rs.getInt("year"));
        student.setEmail(rs.getString("email"));
        return student;
    }
}
