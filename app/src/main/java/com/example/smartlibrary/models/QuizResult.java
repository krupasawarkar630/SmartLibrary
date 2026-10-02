package com.example.smartlibrary.models;

public class QuizResult {
    private String id;
    private String userId;
    private String userName;
    private int score;
    private int totalQuestions;
    private float percentage;
    private long timestamp;

    public QuizResult() {
    }

    public QuizResult(String id, String userId, String userName, int score, int totalQuestions, float percentage, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public float getPercentage() { return percentage; }
    public void setPercentage(float percentage) { this.percentage = percentage; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
