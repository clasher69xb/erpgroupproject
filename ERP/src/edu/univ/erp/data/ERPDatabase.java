
package edu.univ.erp.data;

import java.sql.*;

public class ERPDatabase {
    
    private static final String URL = "jdbc:sqlite:erp_data.db";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("ERP Database connection successful!");
        } catch (SQLException e) {
            System.err.println("ERP Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}