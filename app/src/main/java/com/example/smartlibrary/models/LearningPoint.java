package com.example.smartlibrary.models;

public class LearningPoint {
    private String id;
    private String userId;
    private String bookId;
    private String bookTitle;
    private String insightText;
    private float rating;
    private long timestamp;

    public LearningPoint() {
    }

    public LearningPoint(String id, String userId, String bookId, String bookTitle, String insightText, float rating, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.insightText = insightText;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getInsightText() { return insightText; }
    public void setInsightText(String insightText) { this.insightText = insightText; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
