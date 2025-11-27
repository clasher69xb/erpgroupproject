package edu.univ.erp;

import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.data.DatabaseInitializer;
import edu.univ.erp.util.Logger;

import javax.swing.*;

public class ERPApplication {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseInitializer.initialize();
                System.out.println("Database initialized successfully");
                
                new LoginWindow().setVisible(true);
                
            } catch (Exception e) {
                System.err.println("Failed to initialize application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}