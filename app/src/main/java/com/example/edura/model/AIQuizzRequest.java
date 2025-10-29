package com.example.edura.model;
import java.util.Map;
import java.util.HashMap;

public class AIQuizzRequest {
    private String context;
    private String questionType;
    private String language;
    private String difficulty;
    private int numberOfQuestions;

    public AIQuizzRequest() {}
    public AIQuizzRequest(String context, String questionType, String language, String difficulty, int numberOfQuestions) {
        this.context = context;
        this.questionType = questionType;
        this.language = language;
        this.difficulty = difficulty;
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDifficultly() {
        return difficulty;
    }

    public void setDifficultly(String difficultly) {
        this.difficulty = difficultly;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("context", context);
        map.put("questionType", questionType);
        map.put("language", language);
        map.put("difficulty", difficulty);
        map.put("numberOfQuestions", numberOfQuestions);
        return map;
    }

    public static AIQuizzRequest fromMap(Map<String, Object> map) {
        AIQuizzRequest request = new AIQuizzRequest();
        request.setContext((String) map.get("context"));
        request.setQuestionType((String) map.get("questionType"));
        request.setLanguage((String) map.get("language"));
        request.setDifficultly((String) map.get("difficulty"));
        request.setNumberOfQuestions(map.get("numberOfQuestions") != null ?
                ((Number) map.get("numberOfQuestions")).intValue() : 0);
        return request;
    }

    public boolean isValid(){
        return context != null && !context.isEmpty() &&
                context.trim().split("\\s+").length<=2000 &&
                questionType!=null && !questionType.isEmpty() &&
                language!=null && !language.isEmpty() &&
                difficulty!=null && !difficulty.isEmpty() &&
                numberOfQuestions>=1 && numberOfQuestions<=30;

    }

    public String getValidationError() {
        if (context == null || context.trim().isEmpty()) {
            return "Vui lòng nhập context hoặc topic";
        }
        if (context.trim().split("\\s+").length > 2000) {
            return "Context không được quá 2000 từ";
        }
        if (questionType == null || questionType.trim().isEmpty()) {
            return "Vui lòng chọn loại câu hỏi";
        }
        if (language == null || language.trim().isEmpty()) {
            return "Vui lòng chọn ngôn ngữ";
        }
        if (difficulty == null || difficulty.trim().isEmpty()) {
            return "Vui lòng chọn độ khó";
        }
        if (numberOfQuestions < 1 || numberOfQuestions > 20) {
            return "Số câu hỏi phải từ 1 đến 20";
        }
        return null;
    }

    @Override
    public String toString() {
        return "AiQuizRequest{" +
                "context='" + context + '\'' +
                ", questionType='" + questionType + '\'' +
                ", language='" + language + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", numberOfQuestions=" + numberOfQuestions +
                '}';
    }
}
