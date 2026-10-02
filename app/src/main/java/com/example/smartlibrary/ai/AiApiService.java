package com.example.smartlibrary.ai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AiApiService {
    @POST("api/ai/chat")
    Call<AiChatResponse> sendMessage(
        @Header("Authorization") String authorization,
        @Body AiChatRequest request
    );
}
