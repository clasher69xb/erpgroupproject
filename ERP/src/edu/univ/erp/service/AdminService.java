
package edu.univ.erp.service;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.AuthDatabase;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

public class AdminService {
    
    public static class OperationResult {
        public boolean success;
        public String message;
        public int id;
        
        public OperationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.id = -1;
        }
        
        public OperationResult(boolean success, String message, int id) {
            this.success = success;
            this.message = message;
            this.id = id;
        }
    }
    
    public static OperationResult createStudent(String username, String password, String rollNo, 
                                                 String name, String program, int year, String email) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        // Create user in Auth DB
        String hash = PasswordHasher.hashPassword(password);
        int userId = AuthDatabase.createUserAndGetId(username, "STUDENT", hash);
        
        if (userId == -1) {
            return new OperationResult(false, "Failed to create user account. Username may already exist.");
        }
        
        // Create student profile in ERP DB
        Student student = new Student(userId, rollNo, name, program, year, email);
        boolean created = StudentDAO.create(student);
        
        if (created) {
            return new OperationResult(true, "Student created successfully.", userId);
        } else {
            return new OperationResult(false, "User created but student profile failed.");
        }
    }
    
    public static OperationResult createInstructor(String username, String password, String employeeId,
                                                    String name, String department, String email) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        // Create user in Auth DB
        String hash = PasswordHasher.hashPassword(password);
        int userId = AuthDatabase.createUserAndGetId(username, "INSTRUCTOR", hash);
        
        if (userId == -1) {
            return new OperationResult(false, "Failed to create user account. Username may already exist.");
        }
        
        // Create instructor profile in ERP DB
        Instructor instructor = new Instructor(userId, employeeId, name, department, email);
        boolean created = InstructorDAO.create(instructor);
        
        if (created) {
            return new OperationResult(true, "Instructor created successfully.", userId);
        } else {
            return new OperationResult(false, "User created but instructor profile failed.");
        }
    }
    
    public static OperationResult createCourse(String code, String title, int credits, String description) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        Course course = new Course();
        course.setCode(code);
        course.setTitle(title);
        course.setCredits(credits);
        course.setDescription(description);
        
        boolean created = CourseDAO.create(course);
        
        if (created) {
            return new OperationResult(true, "Course created successfully.", course.getCourseId());
        } else {
            return new OperationResult(false, "Failed to create course. Course code may already exist.");
        }
    }
    
    public static OperationResult createSection(int courseId, Integer instructorId, String dayTime,
                                                 String room, int capacity, String semester, int year) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        if (capacity <= 0) {
            return new OperationResult(false, "Capacity must be positive.");
        }
        
        Section section = new Section();
        section.setCourseId(courseId);
        section.setInstructorId(instructorId);
        section.setDayTime(dayTime);
        section.setRoom(room);
        section.setCapacity(capacity);
        section.setSemester(semester);
        section.setYear(year);
        
        boolean created = SectionDAO.create(section);
        
        if (created) {
            return new OperationResult(true, "Section created successfully.", section.getSectionId());
        } else {
            return new OperationResult(false, "Failed to create section.");
        }
    }
    
    public static OperationResult assignInstructor(int sectionId, int instructorId) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        Section section = SectionDAO.getById(sectionId);
        if (section == null) {
            return new OperationResult(false, "Section not found.");
        }
        
        Instructor instructor = InstructorDAO.getByUserId(instructorId);
        if (instructor == null) {
            return new OperationResult(false, "Instructor not found.");
        }
        
        section.setInstructorId(instructorId);
        boolean updated = SectionDAO.update(section);
        
        if (updated) {
            return new OperationResult(true, "Instructor assigned successfully.");
        } else {
            return new OperationResult(false, "Failed to assign instructor.");
        }
    }
    
    public static OperationResult toggleMaintenanceMode(boolean enabled) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        boolean updated = SettingsDAO.setMaintenanceMode(enabled);
        
        if (updated) {
            String status = enabled ? "enabled" : "disabled";
            return new OperationResult(true, "Maintenance mode " + status + ".");
        } else {
            return new OperationResult(false, "Failed to update maintenance mode.");
        }
    }
    
    public static OperationResult updateCourse(Course course) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        boolean updated = CourseDAO.update(course);
        
        if (updated) {
            return new OperationResult(true, "Course updated successfully.");
        } else {
            return new OperationResult(false, "Failed to update course.");
        }
    }
    
    public static OperationResult updateSection(Section section) {
        if (!AccessChecker.isAdmin()) {
            return new OperationResult(false, "Access Denied: Admin privileges required.");
        }
        
        boolean updated = SectionDAO.update(section);
        
        if (updated) {
            return new OperationResult(true, "Section updated successfully.");
        } else {
            return new OperationResult(false, "Failed to update section.");
        }
    }
}