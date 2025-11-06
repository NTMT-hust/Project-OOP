package com.humanitarian.logistics.test;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

public class YouTubeSearchExample {
    public static void main(String[] args) throws Exception {
        // --- STEP 1: Initialize YouTube Service ---
        YouTube youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                null
        ).setApplicationName("youtube-api-example").build();

        // --- STEP 2: Create Search Request ---
        YouTube.Search.List search = youtube.search().list("id,snippet");

        // --- STEP 3: Set parameters ---
        search.setQ("lofi hip hop");       // Search query
        search.setType("video");           // Only videos
        search.setMaxResults(5L);          // Return 5 results
        search.setKey("YOUR_API_KEY");     // <-- Replace with your API key

        // --- STEP 4: Execute request ---
        SearchListResponse response = search.execute();

        // --- STEP 5: Process results ---
        List<SearchResult> results = response.getItems();
        for (SearchResult result : results) {
            System.out.println("Title: " + result.getSnippet().getTitle());
            System.out.println("Channel: " + result.getSnippet().getChannelTitle());
            System.out.println("Video ID: " + result.getId().getVideoId());
            System.out.println("------------------------------");
        }
    }
}
