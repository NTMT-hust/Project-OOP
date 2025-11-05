package com.humanitarian.logistics;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Humanitarian Logistics Analyzer");
        System.out.println("========================================");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        System.out.println("========================================");
        
        // Test config loading
        try {
            com.humanitarian.logistics.config.AppConfig config = new com.humanitarian.logistics.config.AppConfig();
            System.out.println("✓ Configuration loaded successfully");
        } catch (Exception e) {
            System.err.println("✗ Configuration failed: " + e.getMessage());
        }
    }
}