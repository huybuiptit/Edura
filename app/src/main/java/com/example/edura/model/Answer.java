package com.example.edura.model;

import java.util.HashMap;
import java.util.Map;

public class Answer {
    private String answerText;
    private boolean isCorrect;

    // Empty constructor needed for Firestore
    public Answer() {
    }

    public Answer(String answerText, boolean isCorrect) {
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("answerText", answerText);
        map.put("isCorrect", isCorrect);
        return map;
    }

    // Create from Map
    public static Answer fromMap(Map<String, Object> map) {
        Answer answer = new Answer();
        answer.setAnswerText((String) map.get("answerText"));
        answer.setCorrect((Boolean) map.get("isCorrect"));
        return answer;
    }
}


