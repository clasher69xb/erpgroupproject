package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.*;

public class GradingSystem {
    
    // Grading weights: Quiz 20%, Midterm 30%, Final 50%
    private static final double QUIZ_WEIGHT = 0.20;
    private static final double MIDTERM_WEIGHT = 0.30;
    private static final double FINAL_WEIGHT = 0.50;
    
    public static class GradeInfo {
        public String component;
        public double score;
        public double maxScore;
        public double percentage;
        
        public GradeInfo(String component, double score, double maxScore) {
            this.component = component;
            this.score = score;
            this.maxScore = maxScore;
            this.percentage = maxScore > 0 ? (score / maxScore) * 100 : 0;
        }
    }
    
    public static class FinalGradeCalculation {
        public double totalPercentage;
        public String letterGrade;
        public List<GradeInfo> components;
        
        public FinalGradeCalculation() {
            this.components = new ArrayList<>();
        }
    }
    
    public static FinalGradeCalculation calculateFinalGrade(int enrollmentId) {
        FinalGradeCalculation result = new FinalGradeCalculation();
        List<Grade> grades = GradeDAO.getByEnrollment(enrollmentId);
        
        Map<String, Grade> gradeMap = new HashMap<>();
        for (Grade grade : grades) {
            gradeMap.put(grade.getComponent(), grade);
        }
        
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        
        // Quiz
        if (gradeMap.containsKey("Quiz")) {
            Grade quiz = gradeMap.get("Quiz");
            double percentage = (quiz.getScore() / quiz.getMaxScore()) * 100;
            weightedSum += percentage * QUIZ_WEIGHT;
            totalWeight += QUIZ_WEIGHT;
            result.components.add(new GradeInfo("Quiz", quiz.getScore(), quiz.getMaxScore()));
        }
        
        // Midterm
        if (gradeMap.containsKey("Midterm")) {
            Grade midterm = gradeMap.get("Midterm");
            double percentage = (midterm.getScore() / midterm.getMaxScore()) * 100;
            weightedSum += percentage * MIDTERM_WEIGHT;
            totalWeight += MIDTERM_WEIGHT;
            result.components.add(new GradeInfo("Midterm", midterm.getScore(), midterm.getMaxScore()));
        }
        
        // Final
        if (gradeMap.containsKey("Final")) {
            Grade finalExam = gradeMap.get("Final");
            double percentage = (finalExam.getScore() / finalExam.getMaxScore()) * 100;
            weightedSum += percentage * FINAL_WEIGHT;
            totalWeight += FINAL_WEIGHT;
            result.components.add(new GradeInfo("Final", finalExam.getScore(), finalExam.getMaxScore()));
        }
        
        if (totalWeight > 0) {
            result.totalPercentage = weightedSum / totalWeight;
            result.letterGrade = convertToLetterGrade(result.totalPercentage);
        } else {
            result.totalPercentage = 0.0;
            result.letterGrade = "N/A";
        }
        
        return result;
    }
    
    public static String convertToLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B+";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C+";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }
    
    public static double calculateGPA(String letterGrade) {
        switch (letterGrade) {
            case "A": return 4.0;
            case "B+": return 3.5;
            case "B": return 3.0;
            case "C+": return 2.5;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
    
    public static Map<String, Object> getStudentTranscript(int studentId) {
        Map<String, Object> transcript = new HashMap<>();
        
        Student student = StudentDAO.getByUserId(studentId);
        if (student == null) {
            return transcript;
        }
        
        transcript.put("student", student);
        
        List<Enrollment> enrollments = EnrollmentDAO.getByStudent(studentId);
        List<Map<String, Object>> courseRecords = new ArrayList<>();
        
        double totalGradePoints = 0.0;
        int totalCredits = 0;
        
        for (Enrollment enrollment : enrollments) {
            if ("COMPLETED".equals(enrollment.getStatus()) || "ENROLLED".equals(enrollment.getStatus())) {
                Section section = SectionDAO.getById(enrollment.getSectionId());
                if (section != null) {
                    Course course = CourseDAO.getById(section.getCourseId());
                    if (course != null) {
                        Map<String, Object> record = new HashMap<>();
                        record.put("course", course);
                        record.put("section", section);
                        
                        FinalGradeCalculation gradeCalc = calculateFinalGrade(enrollment.getEnrollmentId());
                        record.put("letterGrade", gradeCalc.letterGrade);
                        record.put("percentage", gradeCalc.totalPercentage);
                        
                        courseRecords.add(record);
                        
                        if (!"N/A".equals(gradeCalc.letterGrade)) {
                            double gpa = calculateGPA(gradeCalc.letterGrade);
                            totalGradePoints += gpa * course.getCredits();
                            totalCredits += course.getCredits();
                        }
                    }
                }
            }
        }
        
        transcript.put("courses", courseRecords);
        transcript.put("totalCredits", totalCredits);
        transcript.put("cgpa", totalCredits > 0 ? totalGradePoints / totalCredits : 0.0);
        
        return transcript;
    }
}