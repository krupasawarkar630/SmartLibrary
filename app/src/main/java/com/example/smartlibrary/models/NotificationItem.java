package com.example.smartlibrary.models;

public class NotificationItem {
    private String id;
    private String userId;
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;
    private String type; // REQUEST_APPROVED, REQUEST_REJECTED, DUE_REMINDER, OVERDUE, ANNOUNCEMENT

    public NotificationItem() {
    }

    public NotificationItem(String id, String userId, String title, String message, long timestamp, boolean isRead, String type) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
