package com.example.edura.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question {
    public enum QuestionType {
        SINGLE_CHOICE,   // Chọn 1 trong 4 đáp án
        MULTIPLE_CHOICE, // Chọn nhiều đáp án
        FILL_IN_BLANK    // Điền vào chỗ trống
    }

    private String questionId;
    private String questionText;
    private QuestionType questionType;
    private List<Answer> answers;

    // Empty constructor needed for Firestore
    public Question() {
        this.answers = new ArrayList<>();
    }

    public Question(String questionText, QuestionType questionType) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.answers = new ArrayList<>();
    }

    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("questionId", questionId);
        map.put("questionText", questionText);
        map.put("questionType", questionType.name());
        
        List<Map<String, Object>> answerMaps = new ArrayList<>();
        for (Answer answer : answers) {
            answerMaps.add(answer.toMap());
        }
        map.put("answers", answerMaps);
        
        return map;
    }

    // Create from Map
    public static Question fromMap(Map<String, Object> map) {
        Question question = new Question();
        question.setQuestionId((String) map.get("questionId"));
        question.setQuestionText((String) map.get("questionText"));
        
        String typeStr = (String) map.get("questionType");
        question.setQuestionType(QuestionType.valueOf(typeStr));
        
        List<Map<String, Object>> answerMaps = (List<Map<String, Object>>) map.get("answers");
        List<Answer> answers = new ArrayList<>();
        if (answerMaps != null) {
            for (Map<String, Object> answerMap : answerMaps) {
                answers.add(Answer.fromMap(answerMap));
            }
        }
        question.setAnswers(answers);
        
        return question;
    }
}


