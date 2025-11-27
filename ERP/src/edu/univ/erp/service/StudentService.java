package edu.univ.erp.service;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.ArrayList;
import java.util.List;

public class StudentService {
    
    public static class EnrollmentResult {
        public boolean success;
        public String message;
        
        public EnrollmentResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
    
    public static List<Section> getAvailableSections() {
        List<Section> sections = SectionDAO.getAll();
        
        // Enhance with course and instructor info
        for (Section section : sections) {
            Course course = CourseDAO.getById(section.getCourseId());
            if (course != null) {
                section.setCourseName(course.getCode() + " - " + course.getTitle());
            }
            
            if (section.getInstructorId() != null) {
                Instructor instructor = InstructorDAO.getByUserId(section.getInstructorId());
                if (instructor != null) {
                    section.setInstructorName(instructor.getName());
                }
            } else {
                section.setInstructorName("TBA");
            }
            
            // Get enrolled count
            section.setEnrolled(SectionDAO.getEnrolledCount(section.getSectionId()));
        }
        
        return sections;
    }
    
    public static EnrollmentResult registerForSection(int studentId, int sectionId) {
        // Check access
        if (!AccessChecker.canModify()) {
            return new EnrollmentResult(false, AccessChecker.getAccessDeniedMessage());
        }
        
        if (!AccessChecker.canAccessStudentData(SessionManager.getInstance().getCurrentUserId(), studentId)) {
            return new EnrollmentResult(false, "Access Denied: Cannot register for another student.");
        }
        
        // Check if already enrolled
        Enrollment existing = EnrollmentDAO.getByStudentAndSection(studentId, sectionId);
        if (existing != null) {
            return new EnrollmentResult(false, "Already enrolled in this section.");
        }
        
        // Check section capacity
        Section section = SectionDAO.getById(sectionId);
        if (section == null) {
            return new EnrollmentResult(false, "Section not found.");
        }
        
        int enrolled = SectionDAO.getEnrolledCount(sectionId);
        if (enrolled >= section.getCapacity()) {
            return new EnrollmentResult(false, "Section is full.");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setSectionId(sectionId);
        enrollment.setStatus("ENROLLED");
        
        boolean created = EnrollmentDAO.create(enrollment);
        if (created) {
            return new EnrollmentResult(true, "Successfully registered for section.");
        } else {
            return new EnrollmentResult(false, "Failed to register. Please try again.");
        }
    }
    
    public static EnrollmentResult dropSection(int studentId, int sectionId) {
        // Check access
        if (!AccessChecker.canModify()) {
            return new EnrollmentResult(false, AccessChecker.getAccessDeniedMessage());
        }
        
        if (!AccessChecker.canAccessStudentData(SessionManager.getInstance().getCurrentUserId(), studentId)) {
            return new EnrollmentResult(false, "Access Denied: Cannot drop for another student.");
        }
        
        // Find enrollment
        Enrollment enrollment = EnrollmentDAO.getByStudentAndSection(studentId, sectionId);
        if (enrollment == null) {
            return new EnrollmentResult(false, "Not enrolled in this section.");
        }
        
        // Update status to DROPPED
        boolean updated = EnrollmentDAO.updateStatus(enrollment.getEnrollmentId(), "DROPPED");
        if (updated) {
            return new EnrollmentResult(true, "Successfully dropped section.");
        } else {
            return new EnrollmentResult(false, "Failed to drop section. Please try again.");
        }
    }
    
    public static List<Section> getStudentSections(int studentId) {
        List<Enrollment> enrollments = EnrollmentDAO.getByStudent(studentId);
        List<Section> sections = new ArrayList<>();
        
        for (Enrollment enrollment : enrollments) {
            if ("ENROLLED".equals(enrollment.getStatus())) {
                Section section = SectionDAO.getById(enrollment.getSectionId());
                if (section != null) {
                    // Enhance with course info
                    Course course = CourseDAO.getById(section.getCourseId());
                    if (course != null) {
                        section.setCourseName(course.getCode() + " - " + course.getTitle());
                    }
                    
                    if (section.getInstructorId() != null) {
                        Instructor instructor = InstructorDAO.getByUserId(section.getInstructorId());
                        if (instructor != null) {
                            section.setInstructorName(instructor.getName());
                        }
                    }
                    
                    sections.add(section);
                }
            }
        }
        
        return sections;
    }
    
    public static List<Grade> getStudentGrades(int studentId, int sectionId) {
        Enrollment enrollment = EnrollmentDAO.getByStudentAndSection(studentId, sectionId);
        if (enrollment == null) {
            return new ArrayList<>();
        }
        
        return GradeDAO.getByEnrollment(enrollment.getEnrollmentId());
    }
}