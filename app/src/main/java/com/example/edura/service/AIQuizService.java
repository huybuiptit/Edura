package com.example.edura.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.edura.model.AIQuizzRequest;
import com.example.edura.model.AIQuizzRespond;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIQuizService {
    
    private static final String TAG = "AIQuizService";
    private static final String API_ENDPOINT = "https://edura-backend-pearl.vercel.app/create_quiz";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final Gson gson;
    private final Handler mainHandler;

    public AIQuizService() {
        // Configure OkHttp with longer timeouts for AI processing
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)    // Connection timeout
                .writeTimeout(30, TimeUnit.SECONDS)      // Write timeout
                .readTimeout(120, TimeUnit.SECONDS)      // Read timeout - AI needs time
                .retryOnConnectionFailure(true)          // Retry on failure
                .build();
        
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        Log.d(TAG, "AIQuizService initialized with extended timeouts");
    }

    public interface AIQuizCallback {
        void onSuccess(AIQuizzRespond response);
        void onError(String error);
    }

    public void generateQuiz(AIQuizzRequest request, AIQuizCallback callback) {
        // Convert request to JSON
        String jsonRequest = gson.toJson(request.toMap());
        
        Log.d(TAG, "Sending request to: " + API_ENDPOINT);
        Log.d(TAG, "Request JSON: " + jsonRequest);
        
        RequestBody body = RequestBody.create(jsonRequest, JSON);
        Request httpRequest = new Request.Builder()
                .url(API_ENDPOINT)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        // Make async call
        long startTime = System.currentTimeMillis();
        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                long duration = System.currentTimeMillis() - startTime;
                Log.e(TAG, "Request failed after " + duration + "ms: " + e.getMessage());
                e.printStackTrace();
                
                String errorMsg;
                if (e instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Timeout: Backend mất quá nhiều thời gian (>2 phút). Vui lòng thử lại hoặc giảm số câu hỏi.";
                } else if (e instanceof java.net.UnknownHostException) {
                    errorMsg = "Không thể kết nối tới server. Kiểm tra internet của bạn.";
                } else if (e instanceof java.net.ConnectException) {
                    errorMsg = "Backend không phản hồi. Server có thể đang bảo trì.";
                } else {
                    errorMsg = "Lỗi kết nối: " + e.getMessage();
                }
                
                mainHandler.post(() -> callback.onError(errorMsg));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long duration = System.currentTimeMillis() - startTime;
                Log.d(TAG, "Response received in " + duration + "ms, code: " + response.code());
                
                if (!response.isSuccessful()) {
                    final String errorMsg;
                    if (response.code() == 500) {
                        errorMsg = "Backend lỗi (500). Backend có thể đang gặp vấn đề.";
                    } else if (response.code() == 404) {
                        errorMsg = "API endpoint không tìm thấy (404). Kiểm tra URL.";
                    } else if (response.code() == 429) {
                        errorMsg = "Quá nhiều request (429). Vui lòng đợi và thử lại.";
                    } else {
                        errorMsg = "Lỗi server: " + response.code();
                    }
                    
                    Log.e(TAG, errorMsg);
                    mainHandler.post(() -> callback.onError(errorMsg));
                    return;
                }

                String responseBody = response.body().string();
                Log.d(TAG, "=== FULL RESPONSE BODY ===");
                Log.d(TAG, responseBody);
                Log.d(TAG, "========================");
                
                try {
                    AIQuizzRespond aiResponse = gson.fromJson(responseBody, AIQuizzRespond.class);
                    
                    if (aiResponse == null) {
                        Log.e(TAG, "Parsed response is null");
                        Log.e(TAG, "Raw response was: " + responseBody);
                        mainHandler.post(() -> callback.onError("Backend trả về dữ liệu không hợp lệ"));
                        return;
                    }
                    
                    Log.d(TAG, "Response parsed - Success: " + aiResponse.isSuccess());
                    Log.d(TAG, "Response error: " + aiResponse.getError());
                    Log.d(TAG, "Response title: " + aiResponse.getQuizTitle());
                    Log.d(TAG, "Response questions count: " + 
                        (aiResponse.getQuestions() != null ? aiResponse.getQuestions().size() : "null"));
                    
                    if (!aiResponse.isSuccess()) {
                        String error = aiResponse.getError();
                        if (error == null || error.trim().isEmpty()) {
                            // Backend returned success:false without error message
                            error = "Backend báo lỗi nhưng không có thông báo chi tiết. " +
                                   "Response: " + responseBody.substring(0, Math.min(200, responseBody.length()));
                        }
                        Log.e(TAG, "API returned error: " + error);
                        final String finalError = error;
                        mainHandler.post(() -> callback.onError(finalError));
                        return;
                    }
                    
                    // Validate response has questions
                    if (aiResponse.getQuestions() == null || aiResponse.getQuestions().isEmpty()) {
                        Log.e(TAG, "Response has success:true but no questions!");
                        mainHandler.post(() -> callback.onError(
                            "Backend không trả về câu hỏi nào. Vui lòng thử với context khác hoặc giảm số câu hỏi."));
                        return;
                    }
                    
                    Log.d(TAG, "Successfully parsed response with " + aiResponse.getQuestions().size() + " questions");
                    mainHandler.post(() -> callback.onSuccess(aiResponse));
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing response", e);
                    Log.e(TAG, "Failed to parse: " + responseBody.substring(0, Math.min(500, responseBody.length())));
                    mainHandler.post(() -> callback.onError(
                        "Lỗi parse dữ liệu: " + e.getMessage() + 
                        ". Backend có thể trả về format không đúng."));
                }
            }
        });
    }
}

