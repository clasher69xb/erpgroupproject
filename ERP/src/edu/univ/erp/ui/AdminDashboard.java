
package edu.univ.erp.ui;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JLabel maintenanceStatusLabel;
    
    public AdminDashboard() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("University ERP - Admin Dashboard");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        
        maintenanceStatusLabel = new JLabel();
        updateMaintenanceStatus();
        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(maintenanceStatusLabel);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Courses", createCoursesPanel());
        tabbedPane.addTab("Sections", createSectionsPanel());
        tabbedPane.addTab("Settings", createSettingsPanel());
        
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
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addStudentButton = new JButton("Add Student");
        JButton addInstructorButton = new JButton("Add Instructor");
        
        addStudentButton.addActionListener(e -> showAddStudentDialog());
        addInstructorButton.addActionListener(e -> showAddInstructorDialog());
        
        buttonPanel.add(addStudentButton);
        buttonPanel.add(addInstructorButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        // Students table
        JPanel studentsPanel = new JPanel(new BorderLayout());
        studentsPanel.setBorder(BorderFactory.createTitledBorder("Students"));
        
        String[] studentColumns = {"User ID", "Roll No", "Name", "Program", "Year", "Email"};
        DefaultTableModel studentModel = new DefaultTableModel(studentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable studentTable = new JTable(studentModel);
        loadStudents(studentModel);
        studentsPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        
        // Instructors table
        JPanel instructorsPanel = new JPanel(new BorderLayout());
        instructorsPanel.setBorder(BorderFactory.createTitledBorder("Instructors"));
        
        String[] instructorColumns = {"User ID", "Employee ID", "Name", "Department", "Email"};
        DefaultTableModel instructorModel = new DefaultTableModel(instructorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable instructorTable = new JTable(instructorModel);
        loadInstructors(instructorModel);
        instructorsPanel.add(new JScrollPane(instructorTable), BorderLayout.CENTER);
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, studentsPanel, instructorsPanel);
        splitPane.setDividerLocation(250);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add Student", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fields
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField rollNoField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField programField = new JTextField(20);
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        JTextField emailField = new JTextField(20);
        
        int row = 0;
        addFormRow(panel, gbc, row++, "Username:", usernameField);
        addFormRow(panel, gbc, row++, "Password:", passwordField);
        addFormRow(panel, gbc, row++, "Roll Number:", rollNoField);
        addFormRow(panel, gbc, row++, "Name:", nameField);
        addFormRow(panel, gbc, row++, "Program:", programField);
        addFormRow(panel, gbc, row++, "Year:", yearSpinner);
        addFormRow(panel, gbc, row++, "Email:", emailField);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");
        
        createButton.addActionListener(e -> {
            AdminService.OperationResult result = AdminService.createStudent(
                usernameField.getText(),
                new String(passwordField.getPassword()),
                rollNoField.getText(),
                nameField.getText(),
                programField.getText(),
                (int) yearSpinner.getValue(),
                emailField.getText()
            );
            
            if (result.success) {
                JOptionPane.showMessageDialog(dialog, result.message);
                dialog.dispose();
                refreshUsersTab();
            } else {
                JOptionPane.showMessageDialog(dialog, result.message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    private void showAddInstructorDialog() {
        JDialog dialog = new JDialog(this, "Add Instructor", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField employeeIdField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField departmentField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        
        int row = 0;
        addFormRow(panel, gbc, row++, "Username:", usernameField);
        addFormRow(panel, gbc, row++, "Password:", passwordField);
        addFormRow(panel, gbc, row++, "Employee ID:", employeeIdField);
        addFormRow(panel, gbc, row++, "Name:", nameField);
        addFormRow(panel, gbc, row++, "Department:", departmentField);
        addFormRow(panel, gbc, row++, "Email:", emailField);
        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");
        
        createButton.addActionListener(e -> {
            AdminService.OperationResult result = AdminService.createInstructor(
                usernameField.getText(),
                new String(passwordField.getPassword()),
                employeeIdField.getText(),
                nameField.getText(),
                departmentField.getText(),
                emailField.getText()
            );
            
            if (result.success) {
                JOptionPane.showMessageDialog(dialog, result.message);
                dialog.dispose();
                refreshUsersTab();
            } else {
                JOptionPane.showMessageDialog(dialog, result.message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        buttonPanel.add(addCourseButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Code", "Title", "Credits", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        loadCourses(model);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showAddCourseDialog() {
        JDialog dialog = new JDialog(this, "Add Course", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField codeField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JSpinner creditsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        JTextArea descArea = new JTextArea(3, 20);
        
        int row = 0;
        addFormRow(panel, gbc, row++, "Code:", codeField);
        addFormRow(panel, gbc, row++, "Title:", titleField);
        addFormRow(panel, gbc, row++, "Credits:", creditsSpinner);
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");
        
        createButton.addActionListener(e -> {
            AdminService.OperationResult result = AdminService.createCourse(
                codeField.getText(),
                titleField.getText(),
                (int) creditsSpinner.getValue(),
                descArea.getText()
            );
            
            if (result.success) {
                JOptionPane.showMessageDialog(dialog, result.message);
                dialog.dispose();
                refreshCoursesTab();
            } else {
                JOptionPane.showMessageDialog(dialog, result.message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    private JPanel createSectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addSectionButton = new JButton("Add Section");
        JButton assignInstructorButton = new JButton("Assign Instructor");
        
        addSectionButton.addActionListener(e -> showAddSectionDialog());
        assignInstructorButton.addActionListener(e -> showAssignInstructorDialog());
        
        buttonPanel.add(addSectionButton);
        buttonPanel.add(assignInstructorButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Course", "Instructor", "Day/Time", "Room", "Capacity", "Semester", "Year"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        loadSections(model);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showAddSectionDialog() {
        JDialog dialog = new JDialog(this, "Add Section", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JComboBox<String> courseCombo = new JComboBox<>();
        List<Course> courses = CourseDAO.getAll();
        for (Course course : courses) {
            courseCombo.addItem(course.getCourseId() + " - " + course.getCode());
        }
        
        JComboBox<String> instructorCombo = new JComboBox<>();
        instructorCombo.addItem("None");
        List<Instructor> instructors = InstructorDAO.getAll();
        for (Instructor instructor : instructors) {
            instructorCombo.addItem(instructor.getUserId() + " - " + instructor.getName());
        }
        
        JTextField dayTimeField = new JTextField(20);
        JTextField roomField = new JTextField(20);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 200, 1));
        
        String[] semesters = {"Fall", "Spring", "Summer"};
        JComboBox<String> semesterCombo = new JComboBox<>(semesters);
        
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 2020, 2030, 1));
        
        int row = 0;
        addFormRow(panel, gbc, row++, "Course:", courseCombo);
        addFormRow(panel, gbc, row++, "Instructor:", instructorCombo);
        addFormRow(panel, gbc, row++, "Day/Time:", dayTimeField);
        addFormRow(panel, gbc, row++, "Room:", roomField);
        addFormRow(panel, gbc, row++, "Capacity:", capacitySpinner);
        addFormRow(panel, gbc, row++, "Semester:", semesterCombo);
        addFormRow(panel, gbc, row++, "Year:", yearSpinner);
        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");
        
        createButton.addActionListener(e -> {
            String courseInfo = (String) courseCombo.getSelectedItem();
            int courseId = Integer.parseInt(courseInfo.split(" - ")[0]);
            
            Integer instructorId = null;
            String instructorInfo = (String) instructorCombo.getSelectedItem();
            if (!"None".equals(instructorInfo)) {
                instructorId = Integer.parseInt(instructorInfo.split(" - ")[0]);
            }
            
            AdminService.OperationResult result = AdminService.createSection(
                courseId,
                instructorId,
                dayTimeField.getText(),
                roomField.getText(),
                (int) capacitySpinner.getValue(),
                (String) semesterCombo.getSelectedItem(),
                (int) yearSpinner.getValue()
            );
            
            if (result.success) {
                JOptionPane.showMessageDialog(dialog, result.message);
                dialog.dispose();
                refreshSectionsTab();
            } else {
                JOptionPane.showMessageDialog(dialog, result.message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    private void showAssignInstructorDialog() {
        JOptionPane.showMessageDialog(this, "Select a section from the table, then choose an instructor to assign.");
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Maintenance Mode
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel maintenanceLabel = new JLabel("Maintenance Mode:");
        maintenanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(maintenanceLabel, gbc);
        
        gbc.gridy = 1;
        JButton toggleMaintenanceButton = new JButton("Toggle Maintenance Mode");
        toggleMaintenanceButton.setPreferredSize(new Dimension(250, 40));
        toggleMaintenanceButton.addActionListener(e -> toggleMaintenanceMode());
        panel.add(toggleMaintenanceButton, gbc);
        
        gbc.gridy = 2;
        JLabel statusLabel = new JLabel();
        updateMaintenanceButtonText(toggleMaintenanceButton, statusLabel);
        panel.add(statusLabel, gbc);
        
        return panel;
    }
    
    private void toggleMaintenanceMode() {
        boolean currentMode = AccessChecker.isMaintenanceMode();
        
        AdminService.OperationResult result = AdminService.toggleMaintenanceMode(!currentMode);
        
        if (result.success) {
            JOptionPane.showMessageDialog(this, result.message, "Success", JOptionPane.INFORMATION_MESSAGE);
            updateMaintenanceStatus();
        } else {
            JOptionPane.showMessageDialog(this, result.message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateMaintenanceStatus() {
        boolean maintenance = AccessChecker.isMaintenanceMode();
        if (maintenance) {
            maintenanceStatusLabel.setText("⚠ Maintenance Mode: ON");
            maintenanceStatusLabel.setForeground(Color.RED);
        } else {
            maintenanceStatusLabel.setText("✓ Maintenance Mode: OFF");
            maintenanceStatusLabel.setForeground(new Color(0, 150, 0));
        }
    }
    
    private void updateMaintenanceButtonText(JButton button, JLabel label) {
        boolean maintenance = AccessChecker.isMaintenanceMode();
        if (maintenance) {
            button.setText("Turn OFF Maintenance Mode");
            label.setText("Status: ON - Students/Instructors cannot make changes");
            label.setForeground(Color.RED);
        } else {
            button.setText("Turn ON Maintenance Mode");
            label.setText("Status: OFF - Normal operations");
            label.setForeground(new Color(0, 150, 0));
        }
    }
    
    private void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);
        List<Student> students = StudentDAO.getAll();
        for (Student student : students) {
            model.addRow(new Object[]{
                student.getUserId(),
                student.getRollNo(),
                student.getName(),
                student.getProgram(),
                student.getYear(),
                student.getEmail()
            });
        }
    }
    
    private void loadInstructors(DefaultTableModel model) {
        model.setRowCount(0);
        List<Instructor> instructors = InstructorDAO.getAll();
        for (Instructor instructor : instructors) {
            model.addRow(new Object[]{
                instructor.getUserId(),
                instructor.getEmployeeId(),
                instructor.getName(),
                instructor.getDepartment(),
                instructor.getEmail()
            });
        }
    }
    
    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0);
        List<Course> courses = CourseDAO.getAll();
        for (Course course : courses) {
            model.addRow(new Object[]{
                course.getCourseId(),
                course.getCode(),
                course.getTitle(),
                course.getCredits(),
                course.getDescription()
            });
        }
    }
    
    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);
        List<Section> sections = SectionDAO.getAll();
        for (Section section : sections) {
            Course course = CourseDAO.getById(section.getCourseId());
            String courseName = course != null ? course.getCode() : "Unknown";
            
            String instructorName = "TBA";
            if (section.getInstructorId() != null) {
                Instructor instructor = InstructorDAO.getByUserId(section.getInstructorId());
                if (instructor != null) {
                    instructorName = instructor.getName();
                }
            }
            
            model.addRow(new Object[]{
                section.getSectionId(),
                courseName,
                instructorName,
                section.getDayTime(),
                section.getRoom(),
                section.getCapacity(),
                section.getSemester(),
                section.getYear()
            });
        }
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }
    
    private void refreshUsersTab() {
        Component comp = tabbedPane.getComponentAt(0);
        if (comp instanceof JPanel) {
            tabbedPane.setComponentAt(0, createUsersPanel());
        }
    }
    
    private void refreshCoursesTab() {
        Component comp = tabbedPane.getComponentAt(1);
        if (comp instanceof JPanel) {
            tabbedPane.setComponentAt(1, createCoursesPanel());
        }
    }
    
    private void refreshSectionsTab() {
        Component comp = tabbedPane.getComponentAt(2);
        if (comp instanceof JPanel) {
            tabbedPane.setComponentAt(2, createSectionsPanel());
        }
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