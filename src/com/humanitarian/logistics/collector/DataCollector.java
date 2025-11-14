package com.humanitarian.logistics.collector;

import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import java.util.List;

public interface DataCollector {
    /**
     * Crawl data by critetia
     * @param criteria: tiêu chí thu thập dữ liệu.
     * @return List<SocialPost>: danh sách bài đăng
     */
    List<SocialPost> collect(SearchCriteria criteria);
    abstract String getSourceName();
    abstract boolean testConnection();
    abstract boolean isConfigured();
}

