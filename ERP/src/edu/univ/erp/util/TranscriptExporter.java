
package edu.univ.erp.util;

import edu.univ.erp.domain.*;
import edu.univ.erp.service.GradingSystem;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class TranscriptExporter {
    
    private static final DecimalFormat df = new DecimalFormat("#.##");
    
    public static boolean exportToCSV(Map<String, Object> transcript, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            Student student = (Student) transcript.get("student");
            
            // Header
            writer.println("University ERP - Official Transcript");
            writer.println("=====================================");
            writer.println();
            writer.println("Student Name," + student.getName());
            writer.println("Roll Number," + student.getRollNo());
            writer.println("Program," + student.getProgram());
            writer.println("Year," + student.getYear());
            writer.println();
            
            // Course records
            writer.println("Course Code,Course Title,Credits,Semester,Year,Grade,Percentage");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> courses = (List<Map<String, Object>>) transcript.get("courses");
            
            for (Map<String, Object> record : courses) {
                Course course = (Course) record.get("course");
                Section section = (Section) record.get("section");
                String letterGrade = (String) record.get("letterGrade");
                double percentage = (double) record.get("percentage");
                
                writer.println(String.format("%s,%s,%d,%s,%d,%s,%s",
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    section.getSemester(),
                    section.getYear(),
                    letterGrade,
                    df.format(percentage)));
            }
            
            writer.println();
            writer.println("Total Credits," + transcript.get("totalCredits"));
            writer.println("CGPA," + df.format(transcript.get("cgpa")));
            
            return true;
        } catch (IOException e) {
            Logger.error("Failed to export transcript", e);
            return false;
        }
    }
    
    public static boolean exportToPDF(Map<String, Object> transcript, String filePath) {
        // For simplicity, we'll create a formatted text file
        // In production, use a library like iText or Apache PDFBox
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            Student student = (Student) transcript.get("student");
            
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println("              UNIVERSITY ERP - OFFICIAL TRANSCRIPT              ");
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println();
            writer.println("Student Name    : " + student.getName());
            writer.println("Roll Number     : " + student.getRollNo());
            writer.println("Program         : " + student.getProgram());
            writer.println("Year            : " + student.getYear());
            writer.println("Email           : " + student.getEmail());
            writer.println();
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println("                         COURSE RECORDS                         ");
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println();
            writer.printf("%-12s %-30s %-8s %-10s %-6s %-10s%n",
                "Course", "Title", "Credits", "Semester", "Grade", "Percentage");
            writer.println("───────────────────────────────────────────────────────────────");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> courses = (List<Map<String, Object>>) transcript.get("courses");
            
            for (Map<String, Object> record : courses) {
                Course course = (Course) record.get("course");
                Section section = (Section) record.get("section");
                String letterGrade = (String) record.get("letterGrade");
                double percentage = (double) record.get("percentage");
                
                writer.printf("%-12s %-30s %-8d %-10s %-6s %-10s%n",
                    course.getCode(),
                    truncate(course.getTitle(), 30),
                    course.getCredits(),
                    section.getSemester() + " " + section.getYear(),
                    letterGrade,
                    df.format(percentage) + "%");
            }
            
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println();
            writer.println("Total Credits Earned: " + transcript.get("totalCredits"));
            writer.println("Cumulative GPA (CGPA): " + df.format(transcript.get("cgpa")));
            writer.println();
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println("            Generated on: " + java.time.LocalDate.now());
            writer.println("═══════════════════════════════════════════════════════════════");
            
            return true;
        } catch (IOException e) {
            Logger.error("Failed to export transcript to PDF", e);
            return false;
        }
    }
    
    public static String exportGradesToCSV(int sectionId, List<Map<String, Object>> studentGrades) {
        StringBuilder csv = new StringBuilder();
        
        csv.append("Roll No,Student Name,Quiz,Midterm,Final,Total %,Letter Grade\n");
        
        for (Map<String, Object> record : studentGrades) {
            Student student = (Student) record.get("student");
            @SuppressWarnings("unchecked")
            List<GradingSystem.GradeInfo> grades = (List<GradingSystem.GradeInfo>) record.get("grades");
            String letterGrade = (String) record.get("letterGrade");
            double totalPercentage = (double) record.get("totalPercentage");
            
            csv.append(student.getRollNo()).append(",");
            csv.append(student.getName()).append(",");
            
            // Add component scores
            String quiz = findComponentScore(grades, "Quiz");
            String midterm = findComponentScore(grades, "Midterm");
            String finalExam = findComponentScore(grades, "Final");
            
            csv.append(quiz).append(",");
            csv.append(midterm).append(",");
            csv.append(finalExam).append(",");
            csv.append(df.format(totalPercentage)).append(",");
            csv.append(letterGrade).append("\n");
        }
        
        return csv.toString();
    }
    
    private static String findComponentScore(List<GradingSystem.GradeInfo> grades, String component) {
        for (GradingSystem.GradeInfo grade : grades) {
            if (grade.component.equals(component)) {
                return df.format(grade.score) + "/" + df.format(grade.maxScore);
            }
        }
        return "-";
    }
    
    private static String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}