package com.humanitarian.logistics.collector;

import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.GoogleCseConfig;
import com.humanitarian.logistics.config.NewsApiConfig;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import java.util.*;

public class GenericCollectorFactory {
    public static List<Collector<SearchCriteria, ?, List<SocialPost>>> getAllCollectors() {
        List<Collector<SearchCriteria, ?, List<SocialPost>>> collectors = new ArrayList<>();
        // collectors.add(new YouTubeCollector(new YouTubeConfig(new
        // AppConfig("youtube"))));
        // collectors.add(new GoogleCseCollector(new GoogleCseConfig(new
        // AppConfig("google.cse"))));
        collectors.add(new NewsCollector(new NewsApiConfig(new AppConfig("newsapi"))));

        return collectors;
    }
}
