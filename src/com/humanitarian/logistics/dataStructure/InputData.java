package com.humanitarian.logistics.dataStructure;

import java.time.LocalDateTime;

public class InputData {
	private String keyWord;
	private String[] hashTags;
	private LocalDateTime startDate, endDate;
	private int maxResult;
	private long maxVideo;
	
	public InputData(String keyWord, String[] hashTags, LocalDateTime startDate, LocalDateTime endDate, int maxResult, long maxVideo) {
		this.keyWord = keyWord;
		this.hashTags = hashTags;
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.maxResult = maxResult;
		this.maxVideo = maxVideo;
	}

	public int getMaxResult() {
		return maxResult;
	}
	
	public long getMaxVideo() {
		return maxVideo;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public String[] getHashTags() {
		return hashTags;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}
}
