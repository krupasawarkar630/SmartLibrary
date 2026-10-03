package com.example.smartlibrary.ai;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiRepository {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    // TODO: Add your Gemini API Key here before running locally! Do NOT commit it to GitHub.
    private static final String API_KEY = "YOUR_GEMINI_API_KEY_HERE";
    private GeminiApiService apiService;

    public AiRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GeminiApiService.class);
    }

    public LiveData<AiChatResponse> sendMessage(String message, String conversationId) {
        MutableLiveData<AiChatResponse> result = new MutableLiveData<>();
        
        com.google.gson.JsonObject part = new com.google.gson.JsonObject();
        part.addProperty("text", message);
        
        com.google.gson.JsonArray parts = new com.google.gson.JsonArray();
        parts.add(part);
        
        com.google.gson.JsonObject content = new com.google.gson.JsonObject();
        content.add("parts", parts);
        
        com.google.gson.JsonArray contents = new com.google.gson.JsonArray();
        contents.add(content);
        
        com.google.gson.JsonObject requestBody = new com.google.gson.JsonObject();
        requestBody.add("contents", contents);

        apiService.generateContent(API_KEY, requestBody).enqueue(new Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(Call<com.google.gson.JsonObject> call, Response<com.google.gson.JsonObject> response) {
                AiChatResponse aiResponse = new AiChatResponse();
                aiResponse.setConversationId(conversationId);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String text = response.body()
                            .getAsJsonArray("candidates").get(0).getAsJsonObject()
                            .getAsJsonObject("content")
                            .getAsJsonArray("parts").get(0).getAsJsonObject()
                            .get("text").getAsString();
                            
                        aiResponse.setSuccess(true);
                        aiResponse.setMessage(text);
                    } catch (Exception e) {
                        aiResponse.setSuccess(false);
                        aiResponse.setMessage("Error parsing AI response.");
                    }
                } else {
                    aiResponse.setSuccess(false);
                    aiResponse.setMessage("Error communicating with AI service. Code: " + response.code());
                }
                result.setValue(aiResponse);
            }

            @Override
            public void onFailure(Call<com.google.gson.JsonObject> call, Throwable t) {
                AiChatResponse errorResponse = new AiChatResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Couldn't connect to the AI service. Please check your internet connection.");
                result.setValue(errorResponse);
            }
        });

        return result;
    }
}
