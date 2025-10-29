package com.example.edura.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz {
    private String quizId;
    private String quizTitle;
    private String createdBy; // User ID
    private long createdAt;
    private long updatedAt;
    private List<Question> questions;

    // Empty constructor needed for Firestore
    public Quiz() {
        this.questions = new ArrayList<>();
    }

    public Quiz(String quizTitle, String createdBy) {
        this.quizTitle = quizTitle;
        this.createdBy = createdBy;
        this.questions = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        this.updatedAt = System.currentTimeMillis();
    }

    public int getQuestionCount() {
        return questions != null ? questions.size() : 0;
    }

    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("quizId", quizId);
        map.put("quizTitle", quizTitle);
        map.put("createdBy", createdBy);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        
        List<Map<String, Object>> questionMaps = new ArrayList<>();
        for (Question question : questions) {
            questionMaps.add(question.toMap());
        }
        map.put("questions", questionMaps);
        
        return map;
    }

    // Create from Map
    public static Quiz fromMap(Map<String, Object> map) {
        Quiz quiz = new Quiz();
        quiz.setQuizId((String) map.get("quizId"));
        quiz.setQuizTitle((String) map.get("quizTitle"));
        quiz.setCreatedBy((String) map.get("createdBy"));
        quiz.setCreatedAt(map.get("createdAt") != null ? safeLongFromNumber(map.get("createdAt")) : 0);
        quiz.setUpdatedAt(map.get("updatedAt") != null ? safeLongFromNumber(map.get("updatedAt")) : 0);
        
        List<Map<String, Object>> questionMaps = (List<Map<String, Object>>) map.get("questions");
        List<Question> questions = new ArrayList<>();
        if (questionMaps != null) {
            for (Map<String, Object> questionMap : questionMaps) {
                questions.add(Question.fromMap(questionMap));
            }
        }
        quiz.setQuestions(questions);
        
        return quiz;
    }
    
    // Helper method to safely convert Number to long
    private static long safeLongFromNumber(Object obj) {
        if (obj == null) return 0L;
        
        if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Double) {
            return ((Double) obj).longValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        
        // Try to parse as string
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}


