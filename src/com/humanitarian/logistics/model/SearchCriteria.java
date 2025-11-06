package com.humanitarian.logistics.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchCriteria {
    private String keyword;
    private List<String> hashtags;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String language;
    private int maxResults;
    
    private SearchCriteria() {
        this.hashtags = new ArrayList<>();
    }
    
    // Getters
    public String getKeyword() { return keyword; }
    public List<String> getHashtags() { return hashtags; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public String getLanguage() { return language; }
    public int getMaxResults() { return maxResults; }
    
    // Builder
    public static class Builder {
        private SearchCriteria criteria = new SearchCriteria();
        
        public Builder keyword(String keyword) {
            criteria.keyword = keyword;
            return this;
        }
        
        public Builder hashtags(String... hashtags) {
            criteria.hashtags = Arrays.asList(hashtags);
            return this;
        }
        
        public Builder dateRange(LocalDateTime start, LocalDateTime end) {
            criteria.startDate = start;
            criteria.endDate = end;
            return this;
        }
        
        public Builder language(String language) {
            criteria.language = language;
            return this;
        }
        
        public Builder maxResults(int maxResults) {
            criteria.maxResults = maxResults;
            return this;
        }
        
        public SearchCriteria build() {
            if (criteria.maxResults <= 0) {
                criteria.maxResults = 100;
            }
            return criteria;
        }
    }
}