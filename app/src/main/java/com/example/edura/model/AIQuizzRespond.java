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
    
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("quizTitle")
    private String quizTitle;
    
    @SerializedName("questions")
    private List<AIQuestion> questions;
    
    @SerializedName("error")
    private String error;
    
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

    public boolean isSuccess() {
        if (success != null) {
            return success;
        }
        
        if (quiz != null && !quiz.isEmpty()) {
            Map<String, Object> firstItem = quiz.get(0);
            if (firstItem != null && firstItem.containsKey("error")) {
                return false;
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
        if (questions != null && !questions.isEmpty()) {
            return questions;
        }
        
        if (quiz != null && !quiz.isEmpty()) {
            return parseQuizArray();
        }
        
        return new ArrayList<>();
    }

    public void setQuestions(List<AIQuestion> questions) {
        this.questions = questions;
    }

    public String getError() {
        if (error != null && !error.trim().isEmpty()) {
            return error;
        }
        
        // Check new format error in quiz array
        if (quiz != null && !quiz.isEmpty()) {
            Map<String, Object> firstItem = quiz.get(0);
            if (firstItem != null && firstItem.containsKey("error")) {
                // Check if we have raw_output and can parse questions from it
                String rawOutput = (String) firstItem.get("raw_output");
                if (rawOutput != null && !rawOutput.isEmpty()) {
                    // Try to parse raw output
                    List<AIQuestion> parsedQuestions = parseGeminiRawOutput(rawOutput);
                    if (!parsedQuestions.isEmpty()) {
                        // Successfully parsed questions from raw_output, no error
                        Log.d(TAG, "Parsed " + parsedQuestions.size() + " questions from raw_output, ignoring backend error");
                        return null;
                    }
                }
                // Could not parse raw_output, return error
                return "Backend error: " + firstItem.get("error") + 
                       (rawOutput != null ? "\nFailed to parse raw output" : "");
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
                        
                        // Get question type from backend, or auto-detect based on fields
                        String questionType = (String) item.get("type");
                        Log.d(TAG, "Backend type field: " + questionType);
                        
                        if (questionType == null || questionType.isEmpty()) {
                            // Auto-detect type based on available fields
                            questionType = autoDetectQuestionType(item);
                        }
                        
                        Log.d(TAG, "Final questionType: " + questionType);
                        question.setQuestionType(questionType);

                        List<AIAnswer> answers = new ArrayList<>();
                        
                        // Parse based on question type
                        if ("MULTIPLE_CHOICE".equalsIgnoreCase(questionType)) {
                            // MULTIPLE_CHOICE: has options and multiple correct answers
                            List<String> options = (List<String>) item.get("options");
                            Object correctAnswersObj = item.get("correct_answers");
                            List<String> correctAnswers = new ArrayList<>();
                            
                            if (correctAnswersObj instanceof List) {
                                correctAnswers = (List<String>) correctAnswersObj;
                            } else if (correctAnswersObj instanceof String) {
                                correctAnswers.add((String) correctAnswersObj);
                            }
                            
                            if (options != null) {
                                for (String opt : options) {
                                    AIAnswer ans = new AIAnswer();
                                    ans.setAnswerText(opt);
                                    ans.setCorrect(correctAnswers.contains(opt));
                                    answers.add(ans);
                                }
                            }
                        } else if ("FILL_IN_BLANK".equalsIgnoreCase(questionType)) {
                            // FILL_IN_BLANK: has answer, answers, or correct_answer field (when no options)
                            Object answerObj = item.get("answer");
                            if (answerObj == null) {
                                answerObj = item.get("answers");
                            }
                            if (answerObj == null) {
                                // Fallback to correct_answer for FILL_IN_BLANK
                                answerObj = item.get("correct_answer");
                            }
                            
                            Log.d(TAG, "FILL_IN_BLANK detected - answerObj: " + answerObj);
                            
                            List<String> correctAnswers = new ArrayList<>();
                            if (answerObj instanceof List) {
                                correctAnswers = (List<String>) answerObj;
                            } else if (answerObj instanceof String) {
                                correctAnswers.add((String) answerObj);
                            }
                            
                            Log.d(TAG, "FILL_IN_BLANK - correctAnswers count: " + correctAnswers.size());
                            
                            // For FILL_IN_BLANK, create answer entries for all correct answers
                            for (String ans : correctAnswers) {
                                AIAnswer aiAnswer = new AIAnswer();
                                aiAnswer.setAnswerText(ans);
                                aiAnswer.setCorrect(true);
                                answers.add(aiAnswer);
                            }
                        } else {
                            // DEFAULT: SINGLE_CHOICE - has options and single correct answer
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
     * Auto-detect question type based on available fields in the data
     * Priority order:
     * 1. If has "answer" or "answers" (no options) -> FILL_IN_BLANK
     * 2. If has "correct_answer" (singular) but no "options" -> FILL_IN_BLANK
     * 3. If has "correct_answers" (plural) -> MULTIPLE_CHOICE
     * 4. Otherwise -> SINGLE_CHOICE
     */
    private String autoDetectQuestionType(Map<String, Object> item) {
        boolean hasAnswer = item.containsKey("answer") || item.containsKey("answers");
        boolean hasCorrectAnswer = item.containsKey("correct_answer");
        boolean hasCorrectAnswers = item.containsKey("correct_answers");
        boolean hasOptions = item.containsKey("options");
        
        Log.d(TAG, "Auto-detect: hasAnswer=" + hasAnswer + ", hasCorrectAnswer=" + hasCorrectAnswer 
                + ", hasCorrectAnswers=" + hasCorrectAnswers + ", hasOptions=" + hasOptions);
        Log.d(TAG, "Item keys: " + item.keySet());
        
        // Priority 1: Check for explicit FILL_IN_BLANK fields
        if (hasAnswer && !hasCorrectAnswer && !hasCorrectAnswers) {
            Log.d(TAG, "Detected: FILL_IN_BLANK (has answer/answers field)");
            return "FILL_IN_BLANK";
        }
        
        // Priority 2: If has correct_answer but NO options -> FILL_IN_BLANK
        if (hasCorrectAnswer && !hasOptions) {
            Log.d(TAG, "Detected: FILL_IN_BLANK (has correct_answer, no options)");
            return "FILL_IN_BLANK";
        }
        
        // Priority 3: Check for MULTIPLE_CHOICE indicators
        if (hasCorrectAnswers) {
            Log.d(TAG, "Detected: MULTIPLE_CHOICE");
            return "MULTIPLE_CHOICE";
        }
        
        // Default to SINGLE_CHOICE
        Log.d(TAG, "Detected: SINGLE_CHOICE (default)");
        return "SINGLE_CHOICE";
    }
    
    /**
     * Parse Gemini's raw output which is in markdown JSON format
     */
    private List<AIQuestion> parseGeminiRawOutput(String rawOutput) {
        List<AIQuestion> result = new ArrayList<>();
        
        if (rawOutput == null || rawOutput.isEmpty()) {
            Log.w(TAG, "Raw output is null or empty");
            return result;
        }
        
        try {
            // Remove markdown code blocks: ```json ... ``` or ``` ... ```
            String cleaned = rawOutput.trim();
            Log.d(TAG, "Raw output length: " + cleaned.length());
            
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7); // Remove ```json
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3); // Remove ```
            }
            
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            
            cleaned = cleaned.trim();
            
            Log.d(TAG, "Cleaned JSON (first 500 chars): " + cleaned.substring(0, Math.min(500, cleaned.length())));
            
            // Parse as array of Gemini question format
            Gson gson = new Gson();
            TypeToken<List<GeminiQuestion>> typeToken = new TypeToken<List<GeminiQuestion>>() {};
            List<GeminiQuestion> geminiQuestions = gson.fromJson(cleaned, typeToken.getType());
            
            // Convert to AIQuestion format
            if (geminiQuestions != null && !geminiQuestions.isEmpty()) {
                for (GeminiQuestion gq : geminiQuestions) {
                    AIQuestion aiQuestion = gq.toAIQuestion();
                    if (aiQuestion != null && aiQuestion.getQuestionText() != null) {
                        result.add(aiQuestion);
                    }
                }
                Log.d(TAG, "Successfully parsed " + result.size() + " questions from raw output");
            } else {
                Log.w(TAG, "No questions found in raw output after parsing");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing Gemini raw output: " + e.getMessage(), e);
            Log.e(TAG, "Raw output that failed to parse: " + rawOutput.substring(0, Math.min(1000, rawOutput.length())));
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
        
        @SerializedName("type")
        private String type; // SINGLE_CHOICE, MULTIPLE_CHOICE, FILL_IN_BLANK
        
        @SerializedName("options")
        private List<String> options;
        
        @SerializedName("correct_answer")
        private String correctAnswer;
        
        @SerializedName("correct_answers")
        private List<String> correctAnswers; // For MULTIPLE_CHOICE
        
        @SerializedName("answer")
        private String answer; // For FILL_IN_BLANK
        
        @SerializedName("answers")
        private List<String> answerList; // For FILL_IN_BLANK (multiple)
        
        public AIQuestion toAIQuestion() {
            AIQuestion aiQuestion = new AIQuestion();
            aiQuestion.setQuestionText(question);
            
            // Determine question type - auto-detect if not specified
            String questionType = type;
            if (questionType == null || questionType.isEmpty()) {
                // Auto-detect based on available fields
                // Priority: FILL_IN_BLANK > MULTIPLE_CHOICE > SINGLE_CHOICE
                boolean hasExplicitAnswer = (answer != null && !answer.isEmpty()) || (answerList != null && !answerList.isEmpty());
                boolean hasCorrectAnswerNoOptions = (correctAnswer != null && !correctAnswer.isEmpty()) && (options == null || options.isEmpty());
                
                if (hasExplicitAnswer || hasCorrectAnswerNoOptions) {
                    questionType = "FILL_IN_BLANK";
                } else if (correctAnswers != null && !correctAnswers.isEmpty()) {
                    questionType = "MULTIPLE_CHOICE";
                } else {
                    questionType = "SINGLE_CHOICE";
                }
            }
            
            Log.d(TAG, "GeminiQuestion.toAIQuestion - type: " + type + ", detected: " + questionType);
            aiQuestion.setQuestionType(questionType);
            
            List<AIAnswer> answers = new ArrayList<>();
            
            // Parse based on question type
            if ("MULTIPLE_CHOICE".equalsIgnoreCase(questionType)) {
                // MULTIPLE_CHOICE: has options and multiple correct answers
                if (options != null) {
                    List<String> correctAns = correctAnswers != null ? correctAnswers : new ArrayList<>();
                    for (String option : options) {
                        AIAnswer answer = new AIAnswer();
                        answer.setAnswerText(option);
                        answer.setCorrect(correctAns.contains(option));
                        answers.add(answer);
                    }
                }
            } else if ("FILL_IN_BLANK".equalsIgnoreCase(questionType)) {
                // FILL_IN_BLANK: has answer, answers, or correctAnswer field
                List<String> correctAns = new ArrayList<>();
                if (answerList != null && !answerList.isEmpty()) {
                    correctAns = answerList;
                } else if (answer != null && !answer.isEmpty()) {
                    correctAns.add(answer);
                } else if (correctAnswer != null && !correctAnswer.isEmpty()) {
                    // Fallback to correctAnswer for FILL_IN_BLANK
                    correctAns.add(correctAnswer);
                }
                
                for (String ans : correctAns) {
                    AIAnswer aiAnswer = new AIAnswer();
                    aiAnswer.setAnswerText(ans);
                    aiAnswer.setCorrect(true);
                    answers.add(aiAnswer);
                }
            } else {
                // DEFAULT: SINGLE_CHOICE - has options and single correct answer
                if (options != null) {
                    for (String option : options) {
                        AIAnswer answer = new AIAnswer();
                        answer.setAnswerText(option);
                        answer.setCorrect(option.equals(correctAnswer));
                        answers.add(answer);
                    }
                }
            }
            
            aiQuestion.setAnswers(answers);
            return aiQuestion;
        }
    }
}
