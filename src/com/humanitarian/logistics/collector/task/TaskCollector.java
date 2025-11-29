package com.humanitarian.logistics.collector.task;

import java.util.List;

import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;

import javafx.concurrent.Task;

public class TaskCollector extends Task<List<SocialPost>> {

	public void setCriteria(SearchCriteria criteria) {
	}

	@Override
	protected List<SocialPost> call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
