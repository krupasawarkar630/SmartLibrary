package com.example.smartlibrary.ai;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    Call<JsonObject> generateContent(
        @Query("key") String apiKey,
        @Body JsonObject requestBody
    );
}
