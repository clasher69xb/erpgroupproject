
package edu.univ.erp.ui;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.*;
import edu.univ.erp.util.TranscriptExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class StudentDashboard extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JLabel maintenanceBanner;
    private Student currentStudent;
    private int userId;
    
    public StudentDashboard() {
        userId = SessionManager.getInstance().getCurrentUserId();
        currentStudent = StudentDAO.getByUserId(userId);
        initComponents();
        checkMaintenanceMode();
    }
    
    private void initComponents() {
        setTitle("University ERP - Student Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header with maintenance banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Student Dashboard - " + currentStudent.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        maintenanceBanner = new JLabel("⚠ MAINTENANCE MODE - View Only");
        maintenanceBanner.setFont(new Font("Arial", Font.BOLD, 14));
        maintenanceBanner.setForeground(Color.WHITE);
        maintenanceBanner.setBackground(new Color(255, 140, 0));
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setHorizontalAlignment(SwingConstants.CENTER);
        maintenanceBanner.setVisible(false);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(maintenanceBanner, BorderLayout.SOUTH);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Course Catalog", createCatalogPanel());
        tabbedPane.addTab("My Registrations", createRegistrationsPanel());
        tabbedPane.addTab("Timetable", createTimetablePanel());
        tabbedPane.addTab("Grades", createGradesPanel());
        tabbedPane.addTab("Transcript", createTranscriptPanel());
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel with logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Section ID", "Course", "Instructor", "Day/Time", "Room", "Enrolled/Capacity", "Semester", "Year"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Load sections
        List<Section> sections = StudentService.getAvailableSections();
        for (Section section : sections) {
            model.addRow(new Object[]{
                section.getSectionId(),
                section.getCourseName(),
                section.getInstructorName(),
                section.getDayTime(),
                section.getRoom(),
                section.getEnrolled() + "/" + section.getCapacity(),
                section.getSemester(),
                section.getYear()
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerButton = new JButton("Register");
        JButton refreshButton = new JButton("Refresh");
        
        registerButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int sectionId = (int) model.getValueAt(selectedRow, 0);
                registerForSection(sectionId, model, table);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a section to register.");
            }
        });
        
        refreshButton.addActionListener(e -> refreshCatalog(model));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void registerForSection(int sectionId, DefaultTableModel model, JTable table) {
        StudentService.EnrollmentResult result = StudentService.registerForSection(userId, sectionId);
        
        if (result.success) {
            JOptionPane.showMessageDialog(this, result.message, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCatalog(model);
        } else {
            JOptionPane.showMessageDialog(this, result.message, "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshCatalog(DefaultTableModel model) {
        model.setRowCount(0);
        List<Section> sections = StudentService.getAvailableSections();
        for (Section section : sections) {
            model.addRow(new Object[]{
                section.getSectionId(),
                section.getCourseName(),
                section.getInstructorName(),
                section.getDayTime(),
                section.getRoom(),
                section.getEnrolled() + "/" + section.getCapacity(),
                section.getSemester(),
                section.getYear()
            });
        }
    }
    
    private JPanel createRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Section ID", "Course", "Instructor", "Day/Time", "Room", "Semester", "Year"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Load registrations
        List<Section> sections = StudentService.getStudentSections(userId);
        for (Section section : sections) {
            model.addRow(new Object[]{
                section.getSectionId(),
                section.getCourseName(),
                section.getInstructorName(),
                section.getDayTime(),
                section.getRoom(),
                section.getSemester(),
                section.getYear()
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton dropButton = new JButton("Drop Section");
        JButton refreshButton = new JButton("Refresh");
        
        dropButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int sectionId = (int) model.getValueAt(selectedRow, 0);
                dropSection(sectionId, model);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a section to drop.");
            }
        });
        
        refreshButton.addActionListener(e -> refreshRegistrations(model));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(dropButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void dropSection(int sectionId, DefaultTableModel model) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to drop this section?",
            "Confirm Drop",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            StudentService.EnrollmentResult result = StudentService.dropSection(userId, sectionId);
            
            if (result.success) {
                JOptionPane.showMessageDialog(this, result.message, "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshRegistrations(model);
            } else {
                JOptionPane.showMessageDialog(this, result.message, "Drop Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshRegistrations(DefaultTableModel model) {
        model.setRowCount(0);
        List<Section> sections = StudentService.getStudentSections(userId);
        for (Section section : sections) {
            model.addRow(new Object[]{
                section.getSectionId(),
                section.getCourseName(),
                section.getInstructorName(),
                section.getDayTime(),
                section.getRoom(),
                section.getSemester(),
                section.getYear()
            });
        }
    }
    
    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea timetableArea = new JTextArea();
        timetableArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        timetableArea.setEditable(false);
        
        StringBuilder timetable = new StringBuilder();
        timetable.append("MY TIMETABLE\n");
        timetable.append("═══════════════════════════════════════════════════════════\n\n");
        
        List<Section> sections = StudentService.getStudentSections(userId);
        if (sections.isEmpty()) {
            timetable.append("No registered sections.\n");
        } else {
            for (Section section : sections) {
                timetable.append("Course: ").append(section.getCourseName()).append("\n");
                timetable.append("Day/Time: ").append(section.getDayTime()).append("\n");
                timetable.append("Room: ").append(section.getRoom()).append("\n");
                timetable.append("Instructor: ").append(section.getInstructorName()).append("\n");
                timetable.append("─────────────────────────────────────────────────────────\n\n");
            }
        }
        
        timetableArea.setText(timetable.toString());
        
        JScrollPane scrollPane = new JScrollPane(timetableArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea gradesArea = new JTextArea();
        gradesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gradesArea.setEditable(false);
        
        StringBuilder grades = new StringBuilder();
        grades.append("MY GRADES\n");
        grades.append("═══════════════════════════════════════════════════════════\n\n");
        
        List<Section> sections = StudentService.getStudentSections(userId);
        
        for (Section section : sections) {
            grades.append("Course: ").append(section.getCourseName()).append("\n");
            grades.append("Instructor: ").append(section.getInstructorName()).append("\n\n");
            
            List<Grade> sectionGrades = StudentService.getStudentGrades(userId, section.getSectionId());
            
            if (sectionGrades.isEmpty()) {
                grades.append("  No grades posted yet.\n");
            } else {
                for (Grade grade : sectionGrades) {
                    grades.append(String.format("  %-15s: %.2f / %.2f (%.1f%%)\n",
                        grade.getComponent(),
                        grade.getScore(),
                        grade.getMaxScore(),
                        (grade.getScore() / grade.getMaxScore()) * 100));
                }
                
                String finalGrade = InstructorService.computeFinalGrade(
                    EnrollmentDAO.getByStudentAndSection(userId, section.getSectionId()).getEnrollmentId()
                );
                grades.append("\n  Final Grade: ").append(finalGrade).append("\n");
            }
            
            grades.append("─────────────────────────────────────────────────────────\n\n");
        }
        
        gradesArea.setText(grades.toString());
        
        JScrollPane scrollPane = new JScrollPane(gradesArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea transcriptArea = new JTextArea();
        transcriptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        transcriptArea.setEditable(false);
        
        Map<String, Object> transcript = GradingSystem.getStudentTranscript(userId);
        
        StringBuilder text = new StringBuilder();
        text.append("OFFICIAL TRANSCRIPT\n");
        text.append("═══════════════════════════════════════════════════════════\n\n");
        text.append("Student: ").append(currentStudent.getName()).append("\n");
        text.append("Roll No: ").append(currentStudent.getRollNo()).append("\n");
        text.append("Program: ").append(currentStudent.getProgram()).append("\n\n");
        text.append("─────────────────────────────────────────────────────────\n\n");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> courses = (List<Map<String, Object>>) transcript.get("courses");
        
        for (Map<String, Object> record : courses) {
            Course course = (Course) record.get("course");
            String letterGrade = (String) record.get("letterGrade");
            
            text.append(String.format("%-12s %-30s Grade: %s\n",
                course.getCode(), course.getTitle(), letterGrade));
        }
        
        text.append("\n─────────────────────────────────────────────────────────\n");
        text.append(String.format("Total Credits: %d\n", transcript.get("totalCredits")));
        text.append(String.format("CGPA: %.2f\n", transcript.get("cgpa")));
        
        transcriptArea.setText(text.toString());
        
        JScrollPane scrollPane = new JScrollPane(transcriptArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportCSVButton = new JButton("Export CSV");
        JButton exportPDFButton = new JButton("Export PDF");
        
        exportCSVButton.addActionListener(e -> exportTranscript("csv", transcript));
        exportPDFButton.addActionListener(e -> exportTranscript("pdf", transcript));
        
        buttonPanel.add(exportCSVButton);
        buttonPanel.add(exportPDFButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void exportTranscript(String format, Map<String, Object> transcript) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("transcript_" + currentStudent.getRollNo() + "." + format));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            boolean success;
            
            if (format.equals("csv")) {
                success = TranscriptExporter.exportToCSV(transcript, file.getAbsolutePath());
            } else {
                success = TranscriptExporter.exportToPDF(transcript, file.getAbsolutePath());
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Transcript exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to export transcript.",
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void checkMaintenanceMode() {
        boolean maintenance = AccessChecker.isMaintenanceMode();
        maintenanceBanner.setVisible(maintenance);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            new edu.univ.erp.ui.auth.LoginWindow().setVisible(true);
            dispose();
        }
    }
}