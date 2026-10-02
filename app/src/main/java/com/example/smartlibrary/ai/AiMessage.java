package com.example.smartlibrary.ai;

import java.io.Serializable;

public class AiMessage implements Serializable {
    private String messageId;
    private String conversationId;
    private String userId;
    private String role; // "USER", "ASSISTANT", "SYSTEM"
    private String content;
    private long createdAt;

    public AiMessage() {}

    public AiMessage(String messageId, String conversationId, String userId, String role, String content, long createdAt) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
