package com.example.smartlibrary.ai;

import java.io.Serializable;

public class AiConversation implements Serializable {
    private String conversationId;
    private String userId;
    private String title;
    private long createdAt;
    private long updatedAt;

    public AiConversation() {}

    public AiConversation(String conversationId, String userId, String title, long createdAt, long updatedAt) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
