package com.example.edura.model;

import java.util.HashMap;
import java.util.Map;

public class Answer {
    private String answerText;
    private boolean correct;


    public Answer() {
    }

    public Answer(String answerText, boolean correct) {
        this.answerText = answerText;
        this.correct = correct;
    }


    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean getCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("answerText", answerText);
        map.put("correct", correct);
        return map;
    }

    // Create from Map
    public static Answer fromMap(Map<String, Object> map) {
        Answer answer = new Answer();
        answer.setAnswerText((String) map.get("answerText"));
        Object correctValue = map.get("correct");
        answer.setCorrect(correctValue != null ? (Boolean) correctValue : false);
        return answer;
    }
}


