
package edu.univ.erp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static boolean enabled = true;
    
    public static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }
    
    public static void info(String message) {
        if (enabled) {
            log("INFO", message);
        }
    }
    
    public static void warn(String message) {
        if (enabled) {
            log("WARN", message);
        }
    }
    
    public static void error(String message) {
        if (enabled) {
            log("ERROR", message);
        }
    }
    
    public static void error(String message, Exception e) {
        if (enabled) {
            log("ERROR", message + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] [" + level + "] " + message);
    }
}