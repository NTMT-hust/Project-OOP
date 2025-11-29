package com.humanitarian.logistics.collector.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.humanitarian.logistics.collector.Collector;
import com.humanitarian.logistics.collector.GoogleCseCollector;
import com.humanitarian.logistics.config.GoogleCseConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.util.CustomRateLimiter;

public class GoogleCseTaskCollector extends TaskCollector {

	private SearchCriteria criteria;
	private GoogleCseCollector googleCseCollector;
	
	public GoogleCseTaskCollector(Collector<?, ?, ?> googleCseCollector) {
		this.googleCseCollector = (GoogleCseCollector) googleCseCollector;
	}
	
	@Override
	public void setCriteria(SearchCriteria criteria) {
		this.criteria = criteria;
	}
	
	@Override
	protected List<SocialPost> call() throws Exception {
		
		GoogleCseConfig config = googleCseCollector.getConfig();
		CustomRateLimiter rateLimiter = googleCseCollector.getRateLimiter();
		
		List<SocialPost> posts = new ArrayList<>();

        updateMessage("Using Google Custom Search Engine");

        // 1. Determine how many we WANT (e.g., 50)
        int maxResults = Math.min(criteria.getMaxResults(), config.getMaxResults());

        // 2. Google CSE pages are fixed at 10 results
        int resultsPerPage = 10;

        // 3. Start at index 1 (Google uses 1-based indexing: 1, 11, 21...)
        int startIndex = 1;
        int totalFetched = 0;

        try {
            // FIX: Loop while we haven't reached OUR limit (maxResults),
            // NOT the API limit (91). The API limit is checked inside.
            while (totalFetched < maxResults) {

                // Rate limiter
                if (rateLimiter != null) {
                    rateLimiter.acquire();
                }

                // Calculate how many to fetch this time (usually 10, unless we only need 4
                // more)
                int toFetch = Math.min(resultsPerPage, maxResults - totalFetched);

                updateMessage("Fetching page starting at index " + startIndex);

                String query = googleCseCollector.buildQuery(criteria);

                // Pass the dynamic 'startIndex' to get the next page
                String url = googleCseCollector.buildSearchUrl(query, toFetch, startIndex);

                List<SocialPost> pagePosts;
                try {
                    pagePosts = googleCseCollector.executeSearchRequest(url, criteria);
                } catch (IOException e) {
                    updateMessage("Network error: " + e.getMessage());
                    break;
                }

                if (pagePosts.isEmpty()) {
                    updateMessage("No more results returned by Google");
                    break;
                }

                posts.addAll(pagePosts);
                totalFetched += pagePosts.size();

                // Stop if we have enough
                if (totalFetched >= maxResults) {
                    break;
                }

                // MATH FOR NEXT PAGE:
                // Move start index forward by 10 (1 -> 11 -> 21)
                startIndex += resultsPerPage;

                // HARD LIMIT CHECK:
                // Google CSE cannot return results past index 100.
                // If startIndex is 91, we fetch 91-100.
                // If startIndex becomes 101, the API will fail, so we stop.
                if (startIndex > 91) {
                    updateMessage("Reached Google CSE 100-result limit.");
                    break;
                }
            }

        } catch (Exception e) {
        	updateMessage("Fatal error in collect(): " + e.getMessage());
        }

        return posts;
	}

}
