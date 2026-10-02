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

    private static final String BASE_URL = "http://10.0.2.2:3000/"; // Update to your backend URL
    private AiApiService apiService;

    public AiRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(AiApiService.class);
    }

    public LiveData<AiChatResponse> sendMessage(String message, String conversationId) {
        MutableLiveData<AiChatResponse> result = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        
        if (user == null) {
            AiChatResponse errorResponse = new AiChatResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("User not authenticated.");
            result.setValue(errorResponse);
            return result;
        }

        user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
            String token = getTokenResult.getToken();
            AiChatRequest request = new AiChatRequest(message, conversationId);
            
            apiService.sendMessage("Bearer " + token, request).enqueue(new Callback<AiChatResponse>() {
                @Override
                public void onResponse(Call<AiChatResponse> call, Response<AiChatResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        result.setValue(response.body());
                    } else {
                        AiChatResponse errorResponse = new AiChatResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage("Error communicating with AI service. Code: " + response.code());
                        result.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<AiChatResponse> call, Throwable t) {
                    AiChatResponse errorResponse = new AiChatResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Couldn't connect to the AI service. Please check your internet connection and try again.");
                    result.setValue(errorResponse);
                }
            });
        }).addOnFailureListener(e -> {
            AiChatResponse errorResponse = new AiChatResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Authentication failed. Please login again.");
            result.setValue(errorResponse);
        });

        return result;
    }
}
