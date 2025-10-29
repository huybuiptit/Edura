package com.example.edura.model;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AIQuizzRespond {
    
    private static final String TAG = "AIQuizzRespond";
    
    // Support both old and new backend format
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("quizTitle")
    private String quizTitle;
    
    @SerializedName("questions")
    private List<AIQuestion> questions;
    
    @SerializedName("error")
    private String error;
    
    // New backend format fields
    @SerializedName("quiz_id")
    private String quizId;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("language")
    private String language;
    
    @SerializedName("difficulty")
    private String difficulty;
    
    @SerializedName("number_of_questions")
    private Integer numberOfQuestions;
    
    @SerializedName("quiz")
    private List<Map<String, Object>> quiz;

    public AIQuizzRespond() {
        this.questions = new ArrayList<>();
    }

    // Getters and Setters
    public boolean isSuccess() {
        // Check old format
        if (success != null) {
            return success;
        }
        
        // Check new format - success if quiz array exists and has data
        if (quiz != null && !quiz.isEmpty()) {
            // Check if first item has error
            Map<String, Object> firstItem = quiz.get(0);
            if (firstItem != null && firstItem.containsKey("error")) {
                return false; // Has error
            }
            return true;
        }
        
        return false;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public List<AIQuestion> getQuestions() {
        // If old format has questions, return them
        if (questions != null && !questions.isEmpty()) {
            return questions;
        }
        
        // Parse new backend format
        if (quiz != null && !quiz.isEmpty()) {
            return parseQuizArray();
        }
        
        return new ArrayList<>();
    }

    public void setQuestions(List<AIQuestion> questions) {
        this.questions = questions;
    }

    public String getError() {
        // Check old format error
        if (error != null && !error.trim().isEmpty()) {
            return error;
        }
        
        // Check new format error in quiz array
        if (quiz != null && !quiz.isEmpty()) {
            Map<String, Object> firstItem = quiz.get(0);
            if (firstItem != null && firstItem.containsKey("error")) {
                String rawOutput = (String) firstItem.get("raw_output");
                return "Backend error: " + firstItem.get("error") + 
                       (rawOutput != null ? "\nAttempting to parse raw output..." : "");
            }
        }
        
        return null;
    }
    
    /**
     * Parse quiz array from new backend format
     */
    private List<AIQuestion> parseQuizArray() {
        List<AIQuestion> result = new ArrayList<>();

        if (quiz == null || quiz.isEmpty()) {
            return result;
        }

        try {
            Map<String, Object> firstItem = quiz.get(0);

            // Check if it's an error response
            if (firstItem.containsKey("error")) {
                String rawOutput = (String) firstItem.get("raw_output");
                if (rawOutput != null && !rawOutput.isEmpty()) {
                    result = parseGeminiRawOutput(rawOutput);
                }
            } else {
                // ✅ Parse direct quiz data (normal case)
                for (Map<String, Object> item : quiz) {
                    try {
                        AIQuestion question = new AIQuestion();
                        question.setQuestionText((String) item.get("question"));
                        question.setQuestionType("Single Choice");

                        List<AIAnswer> answers = new ArrayList<>();
                        List<String> options = (List<String>) item.get("options");
                        String correct = (String) item.get("correct_answer");

                        if (options != null) {
                            for (String opt : options) {
                                AIAnswer ans = new AIAnswer();
                                ans.setAnswerText(opt);
                                ans.setCorrect(opt.equals(correct));
                                answers.add(ans);
                            }
                        }
                        question.setAnswers(answers);
                        result.add(question);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing direct quiz item: " + e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing quiz array: " + e.getMessage(), e);
        }

        return result;
    }
    
    /**
     * Parse Gemini's raw output which is in markdown JSON format
     */
    private List<AIQuestion> parseGeminiRawOutput(String rawOutput) {
        List<AIQuestion> result = new ArrayList<>();
        
        try {
            // Remove markdown code blocks: ```json ... ```
            String cleaned = rawOutput.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7); // Remove ```json
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3); // Remove ```
            }
            
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            
            cleaned = cleaned.trim();
            
            Log.d(TAG, "Cleaned JSON: " + cleaned.substring(0, Math.min(200, cleaned.length())));
            
            // Parse as array of Gemini question format
            Gson gson = new Gson();
            TypeToken<List<GeminiQuestion>> typeToken = new TypeToken<List<GeminiQuestion>>() {};
            List<GeminiQuestion> geminiQuestions = gson.fromJson(cleaned, typeToken.getType());
            
            // Convert to AIQuestion format
            if (geminiQuestions != null) {
                for (GeminiQuestion gq : geminiQuestions) {
                    result.add(gq.toAIQuestion());
                }
            }
            
            Log.d(TAG, "Successfully parsed " + result.size() + " questions from raw output");
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing Gemini raw output: " + e.getMessage(), e);
        }
        
        return result;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Inner class for AI Question format
    public static class AIQuestion {
        @SerializedName("questionText")
        private String questionText;
        
        @SerializedName("questionType")
        private String questionType;
        
        @SerializedName("answers")
        private List<AIAnswer> answers;

        public AIQuestion() {
            this.answers = new ArrayList<>();
        }

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public String getQuestionType() {
            return questionType;
        }

        public void setQuestionType(String questionType) {
            this.questionType = questionType;
        }

        public List<AIAnswer> getAnswers() {
            return answers;
        }

        public void setAnswers(List<AIAnswer> answers) {
            this.answers = answers;
        }
        
        // Convert to app's Question model
        public Question toQuestion() {
            Question.QuestionType type = convertQuestionType(this.questionType);
            Question question = new Question(this.questionText, type);
            
            List<Answer> appAnswers = new ArrayList<>();
            for (AIAnswer aiAnswer : this.answers) {
                appAnswers.add(aiAnswer.toAnswer());
            }
            question.setAnswers(appAnswers);
            
            return question;
        }
        
        private Question.QuestionType convertQuestionType(String type) {
            if (type == null) return Question.QuestionType.SINGLE_CHOICE;
            
            type = type.toLowerCase();
            if (type.contains("multiple")) {
                return Question.QuestionType.MULTIPLE_CHOICE;
            } else if (type.contains("fill") || type.contains("blank")) {
                return Question.QuestionType.FILL_IN_BLANK;
            } else {
                return Question.QuestionType.SINGLE_CHOICE;
            }
        }
    }

    // Inner class for AI Answer format
    public static class AIAnswer {
        @SerializedName("answerText")
        private String answerText;
        
        @SerializedName("correct")
        private boolean correct;

        public AIAnswer() {}

        public String getAnswerText() {
            return answerText;
        }

        public void setAnswerText(String answerText) {
            this.answerText = answerText;
        }

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }
        
        // Convert to app's Answer model
        public Answer toAnswer() {
            return new Answer(this.answerText, this.correct);
        }
    }
    
    // Convert entire response to Quiz model
    public Quiz toQuiz(String userId) {
        String title = this.quizTitle;
        if (title == null || title.trim().isEmpty()) {
            title = "Quiz AI - " + (type != null ? type : "Single Choice");
        }
        
        Quiz quiz = new Quiz(title, userId);
        
        List<Question> appQuestions = new ArrayList<>();
        List<AIQuestion> aiQuestions = getQuestions();
        for (AIQuestion aiQuestion : aiQuestions) {
            appQuestions.add(aiQuestion.toQuestion());
        }
        quiz.setQuestions(appQuestions);
        
        return quiz;
    }
    
    /**
     * Inner class for Gemini's output format
     */
    public static class GeminiQuestion {
        @SerializedName("question")
        private String question;
        
        @SerializedName("options")
        private List<String> options;
        
        @SerializedName("correct_answer")
        private String correctAnswer;
        
        public AIQuestion toAIQuestion() {
            AIQuestion aiQuestion = new AIQuestion();
            aiQuestion.setQuestionText(question);
            aiQuestion.setQuestionType("Single Choice"); // Gemini format is single choice
            
            List<AIAnswer> answers = new ArrayList<>();
            if (options != null) {
                for (String option : options) {
                    AIAnswer answer = new AIAnswer();
                    answer.setAnswerText(option);
                    answer.setCorrect(option.equals(correctAnswer));
                    answers.add(answer);
                }
            }
            aiQuestion.setAnswers(answers);
            
            return aiQuestion;
        }
    }
}
