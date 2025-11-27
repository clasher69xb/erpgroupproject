
package edu.univ.erp.ui;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InstructorDashboard extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JLabel maintenanceBanner;
    private Instructor currentInstructor;
    private int userId;
    
    public InstructorDashboard() {
        userId = SessionManager.getInstance().getCurrentUserId();
        currentInstructor = InstructorDAO.getByUserId(userId);
        initComponents();
        checkMaintenanceMode();
    }
    
    private void initComponents() {
        setTitle("University ERP - Instructor Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Instructor Dashboard - " + currentInstructor.getName());
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
        tabbedPane.addTab("My Sections", createSectionsPanel());
        tabbedPane.addTab("Manage Grades", createGradesPanel());
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createSectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Section ID", "Course", "Day/Time", "Room", "Capacity", "Enrolled", "Semester", "Year"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        
        // Load sections
        List<Section> sections = InstructorService.getInstructorSections(userId);
        for (Section section : sections) {
            model.addRow(new Object[]{
                section.getSectionId(),
                section.getCourseName(),
                section.getDayTime(),
                section.getRoom(),
                section.getCapacity(),
                section.getEnrolled(),
                section.getSemester(),
                section.getYear()
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Section Statistics"));
        
        JButton viewStatsButton = new JButton("View Section Stats");
        viewStatsButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int sectionId = (int) model.getValueAt(selectedRow, 0);
                showSectionStats(sectionId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a section.");
            }
        });
        
        statsPanel.add(viewStatsButton);
        panel.add(statsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void showSectionStats(int sectionId) {
        Map<String, Double> stats = InstructorService.getSectionStats(sectionId);
        
        Section section = SectionDAO.getById(sectionId);
        Course course = CourseDAO.getById(section.getCourseId());
        
        String message = String.format(
            "Section Statistics for %s\n\n" +
            "Enrolled Students: %.0f\n" +
            "Average Score: %.2f%%\n",
            course.getCode(),
            stats.get("enrolled"),
            stats.get("avgScore")
        );
        
        JOptionPane.showMessageDialog(this, message, "Section Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Section selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section:"));
        
        JComboBox<String> sectionCombo = new JComboBox<>();
        List<Section> sections = InstructorService.getInstructorSections(userId);
        for (Section section : sections) {
            sectionCombo.addItem(section.getSectionId() + " - " + section.getCourseName());
        }
        topPanel.add(sectionCombo);
        
        JButton loadStudentsButton = new JButton("Load Students");
        topPanel.add(loadStudentsButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Students table
        String[] columns = {"Roll No", "Name", "Quiz", "Midterm", "Final", "Total %", "Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton enterGradeButton = new JButton("Enter/Update Grade");
        JButton computeFinalButton = new JButton("Compute All Finals");
        
        enterGradeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0 && sectionCombo.getSelectedItem() != null) {
                String rollNo = (String) model.getValueAt(selectedRow, 0);
                String sectionInfo = (String) sectionCombo.getSelectedItem();
                int sectionId = Integer.parseInt(sectionInfo.split(" - ")[0]);
                showGradeEntryDialog(rollNo, sectionId, model);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student.");
            }
        });
        
        computeFinalButton.addActionListener(e -> {
            if (sectionCombo.getSelectedItem() != null) {
                computeAllFinals(model);
            }
        });
        
        buttonPanel.add(enterGradeButton);
        buttonPanel.add(computeFinalButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load students action
        loadStudentsButton.addActionListener(e -> {
            if (sectionCombo.getSelectedItem() != null) {
                String sectionInfo = (String) sectionCombo.getSelectedItem();
                int sectionId = Integer.parseInt(sectionInfo.split(" - ")[0]);
                loadStudents(sectionId, model);
            }
        });
        
        return panel;
    }
    
    private void loadStudents(int sectionId, DefaultTableModel model) {
        model.setRowCount(0);
        
        List<Student> students = InstructorService.getSectionStudents(sectionId);
        
        for (Student student : students) {
            Enrollment enrollment = EnrollmentDAO.getByStudentAndSection(student.getUserId(), sectionId);
            
            if (enrollment != null) {
                List<Grade> grades = GradeDAO.getByEnrollment(enrollment.getEnrollmentId());
                
                String quiz = "-";
                String midterm = "-";
                String finalExam = "-";
                
                for (Grade grade : grades) {
                    String scoreStr = String.format("%.1f/%.1f", grade.getScore(), grade.getMaxScore());
                    switch (grade.getComponent()) {
                        case "Quiz": quiz = scoreStr; break;
                        case "Midterm": midterm = scoreStr; break;
                        case "Final": finalExam = scoreStr; break;
                    }
                }
                
                GradingSystem.FinalGradeCalculation calc = GradingSystem.calculateFinalGrade(enrollment.getEnrollmentId());
                
                model.addRow(new Object[]{
                    student.getRollNo(),
                    student.getName(),
                    quiz,
                    midterm,
                    finalExam,
                    String.format("%.2f", calc.totalPercentage),
                    calc.letterGrade
                });
            }
        }
    }
    
    private void showGradeEntryDialog(String rollNo, int sectionId, DefaultTableModel model) {
        Student student = StudentDAO.getByRollNo(rollNo);
        if (student == null) return;
        
        Enrollment enrollment = EnrollmentDAO.getByStudentAndSection(student.getUserId(), sectionId);
        if (enrollment == null) return;
        
        JDialog dialog = new JDialog(this, "Enter Grade - " + student.getName(), true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Component
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Component:"), gbc);
        
        gbc.gridx = 1;
        String[] components = {"Quiz", "Midterm", "Final"};
        JComboBox<String> componentCombo = new JComboBox<>(components);
        panel.add(componentCombo, gbc);
        
        // Score
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Score:"), gbc);
        
        gbc.gridx = 1;
        JTextField scoreField = new JTextField(10);
        panel.add(scoreField, gbc);
        
        // Max Score
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Max Score:"), gbc);
        
        gbc.gridx = 1;
        JTextField maxScoreField = new JTextField(10);
        maxScoreField.setText("100");
        panel.add(maxScoreField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                String component = (String) componentCombo.getSelectedItem();
                double score = Double.parseDouble(scoreField.getText());
                double maxScore = Double.parseDouble(maxScoreField.getText());
                
                InstructorService.GradeResult result = InstructorService.enterGrade(
                    userId, enrollment.getEnrollmentId(), component, score, maxScore);
                
                if (result.success) {
                    JOptionPane.showMessageDialog(dialog, result.message);
                    loadStudents(sectionId, model);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void computeAllFinals(DefaultTableModel model) {
        JOptionPane.showMessageDialog(this, 
            "Final grades have been computed based on:\nQuiz: 20%, Midterm: 30%, Final: 50%",
            "Computation Complete",
            JOptionPane.INFORMATION_MESSAGE);
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