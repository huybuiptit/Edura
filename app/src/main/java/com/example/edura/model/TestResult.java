package com.example.edura.model;

import java.util.HashMap;
import java.util.Map;

public class TestResult {
    private String resultId;
    private String quizId;
    private String quizTitle;
    private String userId;
    private int totalQuestions;
    private int correctAnswers;
    private int questionsCompleted;
    private long completionTime; // in seconds
    private long timestamp; // when test was taken
    private int scorePercentage;

    public TestResult() {
        // Required empty constructor for Firestore
    }

    public TestResult(String quizId, String quizTitle, String userId, int totalQuestions,
                      int correctAnswers, int questionsCompleted, long completionTime, int scorePercentage) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.userId = userId;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.questionsCompleted = questionsCompleted;
        this.completionTime = completionTime;
        this.scorePercentage = scorePercentage;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getQuestionsCompleted() {
        return questionsCompleted;
    }

    public void setQuestionsCompleted(int questionsCompleted) {
        this.questionsCompleted = questionsCompleted;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(int scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("quizId", quizId);
        map.put("quizTitle", quizTitle);
        map.put("userId", userId);
        map.put("totalQuestions", totalQuestions);
        map.put("correctAnswers", correctAnswers);
        map.put("questionsCompleted", questionsCompleted);
        map.put("completionTime", completionTime);
        map.put("timestamp", timestamp);
        map.put("scorePercentage", scorePercentage);
        return map;
    }

    // Create from Map
    public static TestResult fromMap(Map<String, Object> map) {
        TestResult result = new TestResult();
        result.setQuizId((String) map.get("quizId"));
        result.setQuizTitle((String) map.get("quizTitle"));
        result.setUserId((String) map.get("userId"));
        result.setTotalQuestions(((Long) map.get("totalQuestions")).intValue());
        result.setCorrectAnswers(((Long) map.get("correctAnswers")).intValue());
        result.setQuestionsCompleted(((Long) map.get("questionsCompleted")).intValue());
        result.setCompletionTime((Long) map.get("completionTime"));
        result.setTimestamp((Long) map.get("timestamp"));
        
        // Handle scorePercentage with fallback
        Object scoreObj = map.get("scorePercentage");
        if (scoreObj != null) {
            result.setScorePercentage(((Long) scoreObj).intValue());
        } else {
            // Fallback: calculate from correctAnswers (for old data)
            int total = result.getTotalQuestions();
            int correct = result.getCorrectAnswers();
            result.setScorePercentage(total > 0 ? (correct * 100) / total : 0);
        }
        
        return result;
    }
}

