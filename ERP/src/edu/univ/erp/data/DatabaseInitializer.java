package edu.univ.erp.data;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.store.AuthDatabase;

import java.sql.*;

public class DatabaseInitializer {
    
    public static void initialize() {
        try {
            // Initialize Auth DB
            AuthDatabase.initialize();
            
            // Initialize ERP DB
            initializeERPDatabase();
            
            // Load sample data if databases are empty
            if (isDatabaseEmpty()) {
                loadSampleData();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    private static void initializeERPDatabase() throws SQLException {
        try (Connection conn = ERPDatabase.getConnection()) {
            
            // Create students table
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS students (" +
                "user_id INTEGER PRIMARY KEY, " +
                "roll_no TEXT UNIQUE NOT NULL, " +
                "name TEXT NOT NULL, " +
                "program TEXT NOT NULL, " +
                "year INTEGER NOT NULL, " +
                "email TEXT NOT NULL)"
            );
            
            // Create instructors table
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS instructors (" +
                "user_id INTEGER PRIMARY KEY, " +
                "employee_id TEXT UNIQUE NOT NULL, " +
                "name TEXT NOT NULL, " +
                "department TEXT NOT NULL, " +
                "email TEXT NOT NULL)"
            );
            
            // Create courses table
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS courses (" +
                "course_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT UNIQUE NOT NULL, " +
                "title TEXT NOT NULL, " +
                "credits INTEGER NOT NULL, " +
                "description TEXT)"
            );
            
            // Create sections table
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS sections (" +
                "section_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER NOT NULL, " +
                "instructor_id INTEGER, " +
                "day_time TEXT NOT NULL, " +
                "room TEXT NOT NULL, " +
                "capacity INTEGER NOT NULL, " +
                "semester TEXT NOT NULL, " +
                "year INTEGER NOT NULL, " +
                "FOREIGN KEY(course_id) REFERENCES courses(course_id), " +
                "FOREIGN KEY(instructor_id) REFERENCES instructors(user_id))"
            );
            
            // Create enrollments table
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS enrollments (" +
                "enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL, " +
                "section_id INTEGER NOT NULL, " +
                "status TEXT NOT NULL CHECK(status IN ('ENROLLED', 'DROPPED', 'COMPLETED')), " +
                "enrolled_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(student_id) REFERENCES students(user_id), " +
                "FOREIGN KEY(section_id) REFERENCES sections(section_id), " +
                "UNIQUE(student_id, section_id))"
            );
            
            // Create grades table
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS grades (" +
                "grade_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "enrollment_id INTEGER NOT NULL, " +
                "component TEXT NOT NULL, " +
                "score REAL, " +
                "final_grade TEXT, " +
                "FOREIGN KEY(enrollment_id) REFERENCES enrollments(enrollment_id))"
            );
            
            // Create settings table
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS settings (" +
                "key TEXT PRIMARY KEY, " +
                "value TEXT NOT NULL)"
            );
            
            // Insert default settings
            PreparedStatement ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO settings (key, value) VALUES (?, ?)"
            );
            ps.setString(1, "maintenance_mode");
            ps.setString(2, "false");
            ps.executeUpdate();
            
            ps.setString(1, "current_semester");
            ps.setString(2, "Fall");
            ps.executeUpdate();
            
            ps.setString(1, "current_year");
            ps.setString(2, "2024");
            ps.executeUpdate();
        }
    }
    
    private static boolean isDatabaseEmpty() throws SQLException {
        try (Connection conn = ERPDatabase.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
            return rs.next() && rs.getInt(1) == 0;
        }
    }
    
    private static void loadSampleData() throws SQLException {
        System.out.println("Loading sample data...");
        
        // Create admin user
        AuthService.createUser("admin", "admin123", "ADMIN");
        
        // Create instructor
        AuthService.createUser("prof_smith", "pass123", "INSTRUCTOR");
        int instrId = AuthService.getUserIdByUsername("prof_smith");
        
        try (Connection conn = ERPDatabase.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO instructors VALUES (?, ?, ?, ?, ?)"
            );
            ps.setInt(1, instrId);
            ps.setString(2, "EMP001");
            ps.setString(3, "Dr. John Smith");
            ps.setString(4, "Computer Science");
            ps.setString(5, "john.smith@univ.edu");
            ps.executeUpdate();
        }
        
        // Create students
        AuthService.createUser("student1", "pass123", "STUDENT");
        AuthService.createUser("student2", "pass123", "STUDENT");
        
        int std1Id = AuthService.getUserIdByUsername("student1");
        int std2Id = AuthService.getUserIdByUsername("student2");
        
        try (Connection conn = ERPDatabase.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)"
            );
            
            ps.setInt(1, std1Id);
            ps.setString(2, "2024001");
            ps.setString(3, "Alice Johnson");
            ps.setString(4, "Computer Science");
            ps.setInt(5, 2);
            ps.setString(6, "alice.johnson@univ.edu");
            ps.executeUpdate();
            
            ps.setInt(1, std2Id);
            ps.setString(2, "2024002");
            ps.setString(3, "Bob Williams");
            ps.setString(4, "Computer Science");
            ps.setInt(5, 2);
            ps.setString(6, "bob.williams@univ.edu");
            ps.executeUpdate();
        }
        
        // Create sample courses
        createSampleCourses();
        
        System.out.println("Sample data loaded successfully!");
    }
    
    private static void createSampleCourses() throws SQLException {
        try (Connection conn = ERPDatabase.getConnection()) {
            
            // Insert courses
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO courses (code, title, credits, description) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            
            // Course 1: CS101
            ps.setString(1, "CS101");
            ps.setString(2, "Introduction to Programming");
            ps.setInt(3, 3);
            ps.setString(4, "Fundamentals of programming using Java");
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int cs101Id = rs.next() ? rs.getInt(1) : 0;
            
            // Course 2: CS201
            ps.setString(1, "CS201");
            ps.setString(2, "Data Structures");
            ps.setInt(3, 4);
            ps.setString(4, "Study of data structures and algorithms");
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int cs201Id = rs.next() ? rs.getInt(1) : 0;
            
            // Course 3: MATH101
            ps.setString(1, "MATH101");
            ps.setString(2, "Calculus I");
            ps.setInt(3, 4);
            ps.setString(4, "Introduction to differential calculus");
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int math101Id = rs.next() ? rs.getInt(1) : 0;
            
            // Get instructor ID
            int instrId = AuthService.getUserIdByUsername("prof_smith");
            
            // Create sections
            PreparedStatement secPs = conn.prepareStatement(
                "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            
            // Section for CS101
            secPs.setInt(1, cs101Id);
            secPs.setInt(2, instrId);
            secPs.setString(3, "Mon/Wed 10:00-11:30");
            secPs.setString(4, "Room 101");
            secPs.setInt(5, 30);
            secPs.setString(6, "Fall");
            secPs.setInt(7, 2024);
            secPs.executeUpdate();
            rs = secPs.getGeneratedKeys();
            int sec1Id = rs.next() ? rs.getInt(1) : 0;
            
            // Section for CS201
            secPs.setInt(1, cs201Id);
            secPs.setInt(2, instrId);
            secPs.setString(3, "Tue/Thu 14:00-15:30");
            secPs.setString(4, "Room 202");
            secPs.setInt(5, 25);
            secPs.setString(6, "Fall");
            secPs.setInt(7, 2024);
            secPs.executeUpdate();
            rs = secPs.getGeneratedKeys();
            int sec2Id = rs.next() ? rs.getInt(1) : 0;
            
            // Section for MATH101
            secPs.setInt(1, math101Id);
            secPs.setNull(2, Types.INTEGER); // No instructor yet
            secPs.setString(3, "Mon/Wed/Fri 09:00-10:00");
            secPs.setString(4, "Room 305");
            secPs.setInt(5, 35);
            secPs.setString(6, "Fall");
            secPs.setInt(7, 2024);
            secPs.executeUpdate();
            
            // Enroll student1 in CS101
            int std1Id = AuthService.getUserIdByUsername("student1");
            PreparedStatement enrollPs = conn.prepareStatement(
                "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            enrollPs.setInt(1, std1Id);
            enrollPs.setInt(2, sec1Id);
            enrollPs.setString(3, "ENROLLED");
            enrollPs.executeUpdate();
            rs = enrollPs.getGeneratedKeys();
            
            if (rs.next()) {
                int enrollmentId = rs.getInt(1);
                
                // Add some sample grades
                PreparedStatement gradePs = conn.prepareStatement(
                    "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?)"
                );
                
                gradePs.setInt(1, enrollmentId);
                gradePs.setString(2, "Quiz");
                gradePs.setDouble(3, 85.0);
                gradePs.executeUpdate();
                
                gradePs.setInt(1, enrollmentId);
                gradePs.setString(2, "Midterm");
                gradePs.setDouble(3, 78.5);
                gradePs.executeUpdate();
                
                gradePs.setInt(1, enrollmentId);
                gradePs.setString(2, "Final");
                gradePs.setDouble(3, 88.0);
                gradePs.executeUpdate();
            }
        }
    }
}