package com.example.smartlibrary.ai;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AiViewModel extends ViewModel {
    private AiRepository repository;
    private MutableLiveData<List<AiMessage>> messagesLiveData;
    private List<AiMessage> messagesList;
    private String currentConversationId = null;
    private MutableLiveData<Boolean> isLoading;

    public AiViewModel() {
        repository = new AiRepository();
        messagesLiveData = new MutableLiveData<>();
        messagesList = new ArrayList<>();
        isLoading = new MutableLiveData<>(false);
        messagesLiveData.setValue(messagesList);
    }

    public LiveData<List<AiMessage>> getMessages() {
        return messagesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setConversationId(String conversationId) {
        this.currentConversationId = conversationId;
    }

    public String getConversationId() {
        return currentConversationId;
    }

    public void startNewChat() {
        this.currentConversationId = null;
        messagesList.clear();
        messagesLiveData.setValue(messagesList);
    }

    public void addContextMessage(String text) {
        AiMessage msg = new AiMessage(null, currentConversationId, null, "SYSTEM", text, System.currentTimeMillis());
        messagesList.add(msg);
        messagesLiveData.setValue(messagesList);
    }

    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;
        
        AiMessage userMsg = new AiMessage(null, currentConversationId, null, "USER", text, System.currentTimeMillis());
        messagesList.add(userMsg);
        messagesLiveData.setValue(messagesList);
        
        isLoading.setValue(true);
        
        repository.sendMessage(text, currentConversationId).observeForever(response -> {
            isLoading.setValue(false);
            if (response != null) {
                if (currentConversationId == null && response.getConversationId() != null) {
                    currentConversationId = response.getConversationId();
                }
                
                AiMessage aiMsg = new AiMessage(null, currentConversationId, null, 
                        response.isSuccess() ? "ASSISTANT" : "ERROR", 
                        response.getMessage(), System.currentTimeMillis());
                messagesList.add(aiMsg);
                messagesLiveData.setValue(messagesList);
            }
        });
    }
}
