
package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.AdminDashboard;
import edu.univ.erp.ui.InstructorDashboard;
import edu.univ.erp.ui.StudentDashboard;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;
    
    public LoginWindow() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("University ERP - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(Color.GRAY);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        formPanel.add(loginButton, gbc);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel infoLabel = new JLabel("<html><center>Default Accounts:<br>" +
                "admin / admin123 (Admin)<br>" +
                "prof_smith / pass123 (Instructor)<br>" +
                "student1 / pass123 (Student)</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(infoLabel);
        
        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Event handlers
        loginButton.addActionListener(e -> performLogin());
        
        passwordField.addActionListener(e -> performLogin());
        
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void performLogin() {
        if (loginAttempts >= MAX_ATTEMPTS) {
            JOptionPane.showMessageDialog(this,
                "Too many failed login attempts. Please restart the application.",
                "Account Locked",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }
        
        // Show loading
        loginButton.setEnabled(false);
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(Color.BLUE);
        
        // Perform login in background
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return AuthService.login(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    
                    if (user != null) {
                        statusLabel.setText("Login successful!");
                        statusLabel.setForeground(new Color(0, 150, 0));
                        openDashboard(user);
                    } else {
                        loginAttempts++;
                        int remaining = MAX_ATTEMPTS - loginAttempts;
                        
                        if (remaining > 0) {
                            statusLabel.setText("Incorrect username or password. " + 
                                remaining + " attempts remaining.");
                        } else {
                            statusLabel.setText("Account locked due to too many failed attempts.");
                        }
                        statusLabel.setForeground(Color.RED);
                        passwordField.setText("");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Login failed: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    loginButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            switch (user.getRole().toUpperCase()) {
                case "ADMIN":
                    new AdminDashboard().setVisible(true);
                    break;
                case "INSTRUCTOR":
                    new InstructorDashboard().setVisible(true);
                    break;
                case "STUDENT":
                    new StudentDashboard().setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this,
                        "Unknown role: " + user.getRole(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
            }
            dispose();
        });
    }
}