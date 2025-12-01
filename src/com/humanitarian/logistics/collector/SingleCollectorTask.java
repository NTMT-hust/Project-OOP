package com.humanitarian.logistics.collector;

import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import javafx.concurrent.Task;
import java.util.List;

public class SingleCollectorTask extends Task<List<SocialPost>> {

    private final Collector<SearchCriteria, ?, List<SocialPost>> collector;
    private final SearchCriteria criteria;
    private final String sourceName; // Useful for UI messages (e.g., "YouTube")

    public SingleCollectorTask(Collector<SearchCriteria, ?, List<SocialPost>> collector,
            SearchCriteria criteria,
            String sourceName) {
        this.collector = collector;
        this.criteria = criteria;
        this.sourceName = sourceName;
    }

    @Override
    protected List<SocialPost> call() throws Exception {
        // 1. Initial UI Update
        updateMessage("Connecting to " + sourceName + "...");
        updateProgress(-1, 1); // Indeterminate progress (spinning bar)

        // 2. Check for cancellation
        if (isCancelled()) {
            updateMessage("Cancelled.");
            return null;
        }

        // 3. Run the collection logic
        // This blocks here until the API responds
        List<SocialPost> results = collector.doCollect(criteria);

        // 4. Update UI with success
        if (results != null) {
            updateMessage("Finished! Found " + results.size() + " posts from " + sourceName + ".");
            updateProgress(1, 1); // 100% complete
        } else {
            updateMessage("No results found or error occurred.");
            updateProgress(0, 1);
        }

        return results;
    }
}