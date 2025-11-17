package com.humanitarian.logistics.collector;

/**
 * Abstract base collector với Generics
 * 
 * @param <T> Type của Search Criteria (input)
 * @param <A> Type của API Client (Twitter, YouTube API, etc.)
 * @param <R> Type của Result (output - List<SocialPost>)
 * 
 * Demonstrates:
 * - GENERICS for type safety and flexibility
 * - ABSTRACT CLASS for common behavior
 * - TEMPLATE METHOD PATTERN
 */
public abstract class Collector<T, A, R> {
    
    protected String source;
    protected A apiClient;
    protected boolean initialized;
    
    public Collector(String source) {
        this.source = source;
        this.initialized = false;
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
     * Main collection method
     * TEMPLATE METHOD PATTERN: Defines algorithm structure
     */
    public R collect(T criteria) {
        if (!initialized) {
            log("Client not initialized, initializing now...");
            initializeClient();
        }
        
        if (!testConnection()) {
            log("ERROR: Connection test failed!");
            return getEmptyResult();
        }
        
        log("Starting collection...");
        
        try {
            // Pre-collection hook
            beforeCollect(criteria);
            
            // Actual collection - implemented by subclasses
            R result = doCollect(criteria);
            
            // Post-collection hook
            afterCollect(result);
            
            log("Collection completed successfully");
            return result;
            
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
            handleError(e);
            return getEmptyResult();
        }
    }
    
    /**
     * Actual collection logic - must be implemented
     * ABSTRACT METHOD: Each collector has different implementation
     */
    protected abstract R doCollect(T criteria) throws Exception;
    
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