package com.humanitarian.logistics.collector;

import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.*;

import com.humanitarian.logistics.collector.task.*;
import com.humanitarian.logistics.config.AppConfig;
import com.humanitarian.logistics.config.GoogleCseConfig;
import com.humanitarian.logistics.config.NewsApiConfig;
import com.humanitarian.logistics.config.YouTubeConfig;
import com.humanitarian.logistics.model.*;

public class Crawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    private final List<Collector<SearchCriteria, ?, List<SocialPost>>> collectors = new ArrayList<>();

    public Crawler() {
        collectors.add(new GoogleCseCollector(new GoogleCseConfig(new AppConfig("google.cse"))));
        collectors.add(new YouTubeCollector(new YouTubeConfig(new AppConfig("youtube"))));
        collectors.add(new NewsCollector(new NewsApiConfig(new AppConfig("newsapi"))));
    }

    public void runCollectors() {
        SearchCriteria criteria = new SearchCriteria.Builder()
                .keyword(getKeyword())
                .hashtags()
                .dateRange(
                        LocalDateTime.of(2025, 9, 6, 0, 0),
                        LocalDateTime.of(2025, 12, 15, 23, 59))
                .language("vi")
                .maxResults(200000000)
                .build();
        for (Collector<SearchCriteria, ?, List<SocialPost>> c : collectors) {
            logger.info(c.getSource());
            c.collect(criteria);
        }
    }

    private String getKeyword() {
        return "";
    }

    private List<String> getKeywords() {
        return new ArrayList<>();
    }
}
