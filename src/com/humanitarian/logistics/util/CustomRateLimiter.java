// util/RateLimiter.java
package com.humanitarian.logistics.util;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CustomRateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
    
    private final RateLimiter limiter;
    private final int requestsPerWindow;
    private final int windowMinutes;
    
    public CustomRateLimiter(int requestsPerWindow, int windowMinutes) {
        this.requestsPerWindow = requestsPerWindow;
        this.windowMinutes = windowMinutes;
        
        // Calculate permits per second
        double permitsPerSecond = (double) requestsPerWindow / (windowMinutes * 60);
        this.limiter = RateLimiter.create(permitsPerSecond);
        
        logger.info("Rate limiter initialized: {} requests per {} minutes", 
                    requestsPerWindow, windowMinutes);
    }
    
    /**
     * Acquire a permit (blocks if necessary)
     */
    public void acquire() throws InterruptedException {
        double waitTime = limiter.acquire();
        if (waitTime > 0) {
            logger.debug("Rate limited. Waited {} seconds", waitTime);
        }
    }
    
    /**
     * Try to acquire without blocking
     */
    public boolean tryAcquire(long timeout, TimeUnit unit) {
        return limiter.tryAcquire(timeout, unit);
    }
    
    /**
     * Get current rate
     */
    public double getRate() {
        return limiter.getRate();
    }
}