package com.humanitarian.logistics.collector.task;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.collector.NewsCollector;
import com.humanitarian.logistics.config.NewsApiConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.util.CustomRateLimiter;

import javafx.concurrent.Task;

public class NewsApiTaskCollector extends TaskCollector {
	
	private SearchCriteria criteria;
	private NewsCollector newsCollector;
	
	public NewsApiTaskCollector(Collector newsCollector) {
		this.newsCollector = (NewsCollector) newsCollector;
	}
	
	public void setCriteria(SearchCriteria criteria) {
		this.criteria = criteria;
	}

	protected List<SocialPost> call() throws Exception {
		
		NewsApiConfig config = newsCollector.getConfig();
		CustomRateLimiter rateLimiter = newsCollector.getRateLimiter();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		List<SocialPost> posts = new ArrayList<>();
        updateMessage("Collecting ....");
        int maxResults = Math.min(criteria.getMaxResults(), config.getMaxResults());
        int pageSize = config.getDefaultPageSize();
        int page = 1;
        int totalFetched = 0;

        while (totalFetched < maxResults) {
            if (rateLimiter != null) {
                try {
                    rateLimiter.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // good practice
                    updateMessage("Thread interrupted while acquiring permit");
                }
            }
            int toFetch = Math.min(pageSize, maxResults - totalFetched);

            updateMessage("Fetching page " + page + " size " + toFetch);
            // String url = buildTopHeadlinesUrl(buildQuery(criteria), pageSize);
            // Build URL

            String url = newsCollector.buildEverythingUrl(
                    newsCollector.buildQuery(criteria),
                    page,
                    criteria.getStartDate().format(formatter),
                    criteria.getEndDate().format(formatter));

            List<SocialPost> pagePosts;
            try {
                pagePosts = newsCollector.executeRequest(url);
            } catch (IOException e) {
                updateMessage("IOException, execute request");
                pagePosts = new ArrayList<>();
            }
            if (pagePosts.isEmpty()) {
                updateMessage("No more articles available");
                break;
            }
            posts.addAll(pagePosts);
            totalFetched += pagePosts.size();

            updateMessage("Fetched " + pagePosts.size() + " articles (total: " + totalFetched +")");

            // If we got less than pageSize, no more pages
            if (pagePosts.size() < pageSize) {
                break;
            }

            page++;

            // Small delay between requests
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                updateMessage("Thread was interrupted: " + e.getMessage());
            }

        }
        updateMessage("Collected " + posts.size() + " articles from NewsAPI");
        return posts;
	}

}
