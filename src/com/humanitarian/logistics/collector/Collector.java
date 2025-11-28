package com.humanitarian.logistics.collector;

/**
 * Abstract base collector với Generics
 * 
 * @param <T> Type của Search Criteria (input)
 * @param <A> Type của API Client (Twitter, YouTube API, etc.)
 * @param <R> Type của Result (output - List<SocialPost>)
 * 
 *            Demonstrates:
 *            - GENERICS for type safety and flexibility
 *            - ABSTRACT CLASS for common behavior
 *            - TEMPLATE METHOD PATTERN
 */
public abstract class Collector<T, A, R> {

    protected String source;
    protected A apiClient;
    protected boolean initialized = true;

    public Collector(String source) {
        this.source = source;
        this.initialized = true;
    }

    public String getSource() {
        return source;
    }

    /**
     * Initialize API client - must be implemented by subclasses
     * TEMPLATE METHOD: Different for each API
     */
    public abstract void initializeClient();

    /**
     * Test connection to API
     * TEMPLATE METHOD: Different for each API
     */
    public abstract boolean testConnection();

    /**
     * Get API client
     */
    public A getApiClient() {
        return apiClient;
    }

    /**
     * Check if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    // ==========================================
    // HOOK METHODS - Can be overridden
    // ==========================================

    /**
     * Hook: Called before collection
     * Subclasses can override for custom behavior
     */
    protected void beforeCollect(T criteria) {
        log("Preparing to collect with criteria: " + criteria);
    }

    /**
     * Hook: Called after collection
     * Subclasses can override for custom behavior
     */
    protected void afterCollect(R result) {
        log("Post-processing result");
    }

    /**
     * Hook: Error handling
     * Subclasses can override for custom error handling
     */
    protected void handleError(Exception e) {
        log("Error occurred: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Get empty result when collection fails
     * Must be implemented by subclasses
     */
    protected abstract R getEmptyResult();

    // ==========================================
    // UTILITY METHODS
    // ==========================================

    /**
     * Logging utility
     */
    public void log(String message) {
        System.out.println("[" + source.toUpperCase() + "] " + message);
    }

    /**
     * Log with level
     */
    public void log(LogLevel level, String message) {
        String prefix = String.format("[%s][%s]", source.toUpperCase(), level);
        System.out.println(prefix + " " + message);
    }

    /**
     * Log levels
     */
    public enum LogLevel {
        INFO, WARN, ERROR, DEBUG
    }
}