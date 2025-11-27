package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {
    
    public static Section getById(int sectionId) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractSection(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Section> getAllByCourse(int courseId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                sections.add(extractSection(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sections;
    }
    
    public static List<Section> getAllByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE instructor_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                sections.add(extractSection(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sections;
    }
    
    public static List<Section> getAllBySemesterYear(String semester, int year) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE semester = ? AND year = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, semester);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                sections.add(extractSection(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sections;
    }
    
    public static List<Section> getAll() {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM sections ORDER BY year DESC, semester, section_id";
        
        try (Connection conn = ERPDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sections.add(extractSection(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sections;
    }
    
    public static int getEnrolledCount(int sectionId) {
        String sql = "SELECT COUNT(*) as count FROM enrollments WHERE section_id = ? AND status = 'enrolled'";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static boolean create(Section section) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, section.getCourseId());
            ps.setInt(2, section.getInstructorId());
            ps.setString(3, section.getDayTime());
            ps.setString(4, section.getRoom());
            ps.setInt(5, section.getCapacity());
            ps.setString(6, section.getSemester());
            ps.setInt(7, section.getYear());
            
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    section.setSectionId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean update(Section section) {
        String sql = "UPDATE sections SET course_id = ?, instructor_id = ?, day_time = ?, room = ?, " +
                    "capacity = ?, semester = ?, year = ? WHERE section_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, section.getCourseId());
            ps.setInt(2, section.getInstructorId());
            ps.setString(3, section.getDayTime());
            ps.setString(4, section.getRoom());
            ps.setInt(5, section.getCapacity());
            ps.setString(6, section.getSemester());
            ps.setInt(7, section.getYear());
            ps.setInt(8, section.getSectionId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean delete(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        
        try (Connection conn = ERPDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Section extractSection(ResultSet rs) throws SQLException {
        Section section = new Section();
        section.setSectionId(rs.getInt("section_id"));
        section.setCourseId(rs.getInt("course_id"));
        section.setInstructorId(rs.getInt("instructor_id"));
        section.setDayTime(rs.getString("day_time"));
        section.setRoom(rs.getString("room"));
        section.setCapacity(rs.getInt("capacity"));
        section.setSemester(rs.getString("semester"));
        section.setYear(rs.getInt("year"));
        return section;
    }
}