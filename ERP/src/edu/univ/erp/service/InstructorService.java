
package edu.univ.erp.service;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.*;

public class InstructorService {
    
    public static class GradeResult {
        public boolean success;
        public String message;
        
        public GradeResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
    
    /**
     * Get all sections taught by an instructor
     */
    public static List<Section> getInstructorSections(int instructorUserId) {
        List<Section> sections = SectionDAO.getAllByInstructor(instructorUserId);
        
        // Enhance with course information
        for (Section section : sections) {
            Course course = CourseDAO.getById(section.getCourseId());
            if (course != null) {
                section.setCourseName(course.getCode() + " - " + course.getTitle());
            }
            
            // Get enrolled count
            section.setEnrolled(SectionDAO.getEnrolledCount(section.getSectionId()));
        }
        
        return sections;
    }
    
    /**
     * Get all students enrolled in a section
     */
    public static List<Student> getSectionStudents(int sectionId) {
        List<Student> students = new ArrayList<>();
        List<Enrollment> enrollments = EnrollmentDAO.getBySection(sectionId);
        
        for (Enrollment enrollment : enrollments) {
            if ("ENROLLED".equals(enrollment.getStatus())) {
                Student student = StudentDAO.getByUserId(enrollment.getStudentId());
                if (student != null) {
                    students.add(student);
                }
            }
        }
        
        return students;
    }
    
    /**
     * Enter or update a grade for a student
     */
    public static GradeResult enterGrade(int instructorUserId, int enrollmentId, 
                                         String component, double score, double maxScore) {
        // Check access
        if (!AccessChecker.canModify()) {
            return new GradeResult(false, AccessChecker.getAccessDeniedMessage());
        }
        
        if (!AccessChecker.isInstructor() && !AccessChecker.isAdmin()) {
            return new GradeResult(false, "Access Denied: Only instructors can enter grades.");
        }
        
        // Verify instructor owns this section
        Enrollment enrollment = EnrollmentDAO.getById(enrollmentId);
        if (enrollment == null) {
            return new GradeResult(false, "Enrollment not found.");
        }
        
        Section section = SectionDAO.getById(enrollment.getSectionId());
        if (section == null) {
            return new GradeResult(false, "Section not found.");
        }
        
        if (!AccessChecker.isAdmin() && 
            !AccessChecker.canAccessInstructorSection(instructorUserId, section.getInstructorId())) {
            return new GradeResult(false, "Access Denied: You can only grade students in your own sections.");
        }
        
        // Validate scores
        if (score < 0 || maxScore <= 0 || score > maxScore) {
            return new GradeResult(false, "Invalid scores. Score must be between 0 and max score.");
        }
        
        // Check if grade already exists
        Grade existingGrade = GradeDAO.getByEnrollmentAndComponent(enrollmentId, component);
        
        if (existingGrade != null) {
            // Update existing grade
            existingGrade.setScore(score);
            existingGrade.setMaxScore(maxScore);
            boolean updated = GradeDAO.update(existingGrade);
            
            if (updated) {
                return new GradeResult(true, "Grade updated successfully.");
            } else {
                return new GradeResult(false, "Failed to update grade.");
            }
        } else {
            // Create new grade
            Grade grade = new Grade(enrollmentId, component, score);
            grade.setMaxScore(maxScore);
            boolean created = GradeDAO.create(grade);
            
            if (created) {
                return new GradeResult(true, "Grade entered successfully.");
            } else {
                return new GradeResult(false, "Failed to enter grade.");
            }
        }
    }
    
    /**
     * Compute final grade for an enrollment
     */
    public static String computeFinalGrade(int enrollmentId) {
        GradingSystem.FinalGradeCalculation calc = GradingSystem.calculateFinalGrade(enrollmentId);
        return calc.letterGrade;
    }
    
    /**
     * Get statistics for a section
     */
    public static Map<String, Double> getSectionStats(int sectionId) {
        Map<String, Double> stats = new HashMap<>();
        
        List<Enrollment> enrollments = EnrollmentDAO.getBySection(sectionId);
        
        int enrolledCount = 0;
        double totalScore = 0.0;
        int gradeCount = 0;
        
        for (Enrollment enrollment : enrollments) {
            if ("ENROLLED".equals(enrollment.getStatus())) {
                enrolledCount++;
                
                GradingSystem.FinalGradeCalculation calc = 
                    GradingSystem.calculateFinalGrade(enrollment.getEnrollmentId());
                
                if (calc.totalPercentage > 0) {
                    totalScore += calc.totalPercentage;
                    gradeCount++;
                }
            }
        }
        
        stats.put("enrolled", (double) enrolledCount);
        stats.put("avgScore", gradeCount > 0 ? totalScore / gradeCount : 0.0);
        
        return stats;
    }
    
    /**
     * Get detailed grade information for all students in a section
     */
    public static List<Map<String, Object>> getSectionGradesDetailed(int sectionId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Enrollment> enrollments = EnrollmentDAO.getBySection(sectionId);
        
        for (Enrollment enrollment : enrollments) {
            if ("ENROLLED".equals(enrollment.getStatus())) {
                Student student = StudentDAO.getByUserId(enrollment.getStudentId());
                if (student != null) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("student", student);
                    
                    GradingSystem.FinalGradeCalculation calc = 
                        GradingSystem.calculateFinalGrade(enrollment.getEnrollmentId());
                    
                    record.put("grades", calc.components);
                    record.put("totalPercentage", calc.totalPercentage);
                    record.put("letterGrade", calc.letterGrade);
                    
                    result.add(record);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Export section grades to CSV format
     */
    public static String exportSectionGradesCSV(int sectionId) {
        List<Map<String, Object>> grades = getSectionGradesDetailed(sectionId);
        return edu.univ.erp.util.TranscriptExporter.exportGradesToCSV(sectionId, grades);
    }
}