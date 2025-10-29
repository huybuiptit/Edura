package com.example.edura;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edura.model.Answer;
import com.example.edura.model.Question;
import com.example.edura.model.Quiz;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateQuizActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etQuizTitle;
    private LinearLayout questionsContainer;
    private Button btnAddQuestion, btnCreateQuiz;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    private List<QuestionView> questionViews = new ArrayList<>();
    
    private String quizIdToEdit = null; // For edit mode
    private boolean isAIGeneratedQuiz = false; // For AI-generated quiz mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Check if editing existing quiz
        quizIdToEdit = getIntent().getStringExtra("quizId");
        
        // Check if AI-generated quiz
        isAIGeneratedQuiz = getIntent().getBooleanExtra("aiGeneratedQuiz", false);
        
        initViews();
        setupListeners();
        
        // Load quiz data based on mode
        if (isAIGeneratedQuiz) {
            loadAIGeneratedQuiz();
        } else if (quizIdToEdit != null) {
            loadQuizForEdit();
        } else {
            // Add first default question for manual creation
            addQuestionCard();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etQuizTitle = findViewById(R.id.etQuizTitle);
        questionsContainer = findViewById(R.id.questionsContainer);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnCreateQuiz = findViewById(R.id.btnCreateQuiz);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAddQuestion.setOnClickListener(v -> addQuestionCard());

        btnCreateQuiz.setOnClickListener(v -> {
            if (quizIdToEdit != null) {
                updateQuiz();
            } else if (isAIGeneratedQuiz) {
                saveAIGeneratedQuiz();
            } else {
                createQuiz();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                int[] scrcoords = new int[2];
                v.getLocationOnScreen(scrcoords);
                float x = event.getRawX() + v.getLeft() - scrcoords[0];
                float y = event.getRawY() + v.getTop() - scrcoords[1];

                if (x < v.getLeft() || x >= v.getRight() || y < v.getTop() || y > v.getBottom()) {
                    hideKeyboard(v);
                    v.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addQuestionCard() {
        addQuestionCard(Question.QuestionType.SINGLE_CHOICE);
    }
    
    private void addQuestionCard(Question.QuestionType questionType) {
        View questionCard;
        if (questionType == Question.QuestionType.FILL_IN_BLANK) {
            questionCard = LayoutInflater.from(this).inflate(R.layout.item_question_fill_blank, questionsContainer, false);
        } else {
            questionCard = LayoutInflater.from(this).inflate(R.layout.item_question_edit, questionsContainer, false);
        }
        
        QuestionView questionView = new QuestionView(questionCard, questionViews.size() + 1, questionType);
        questionViews.add(questionView);
        
        questionsContainer.addView(questionCard);
        
        // For fill in blank, we don't add default answers (they're in the layout)
        // For other types, add 4 default answers
        if (questionType != Question.QuestionType.FILL_IN_BLANK) {
            for (int i = 0; i < 4; i++) {
                questionView.addAnswerField();
            }
        }
    }

    private void createQuiz() {
        String title = etQuizTitle.getText() != null ? etQuizTitle.getText().toString().trim() : "";
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Question> questions = collectQuestions();
        if (questions.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất 1 câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
        Quiz quiz = new Quiz(title, userId);
        quiz.setQuestions(questions);

        db.collection("quizzes")
                .add(quiz.toMap())
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tạo quiz thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateQuiz() {
        String title = etQuizTitle.getText() != null ? etQuizTitle.getText().toString().trim() : "";
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Question> questions = collectQuestions();
        if (questions.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất 1 câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("quizzes")
                .document(quizIdToEdit)
                .update("quizTitle", title, 
                        "questions", questions.stream().map(Question::toMap).collect(java.util.stream.Collectors.toList()),
                        "updatedAt", System.currentTimeMillis())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật quiz thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private List<Question> collectQuestions() {
        List<Question> questions = new ArrayList<>();
        
        for (QuestionView qv : questionViews) {
            String questionText = qv.getQuestionText();
            if (questionText.isEmpty()) continue;
            
            List<Answer> answers = qv.getAnswers();
            if (answers.isEmpty()) continue;
            
            Question question = new Question(questionText, qv.getQuestionType());
            question.setAnswers(answers);
            questions.add(question);
        }
        
        return questions;
    }

    private void loadQuizForEdit() {
        db.collection("quizzes")
                .document(quizIdToEdit)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Quiz quiz = Quiz.fromMap(documentSnapshot.getData());
                        etQuizTitle.setText(quiz.getQuizTitle());
                        
                        // Clear default question
                        questionsContainer.removeAllViews();
                        questionViews.clear();
                        
                        // Load questions
                        for (Question question : quiz.getQuestions()) {
                            Question.QuestionType type = question.getQuestionType() != null ? question.getQuestionType() : Question.QuestionType.SINGLE_CHOICE;
                            addQuestionCard(type);
                            QuestionView lastQv = questionViews.get(questionViews.size() - 1);
                            lastQv.setQuestionText(question.getQuestionText());
                            
                            if (type != Question.QuestionType.FILL_IN_BLANK) {
                                lastQv.clearAnswers();
                                for (Answer answer : question.getAnswers()) {
                                    lastQv.addAnswerField(answer.getAnswerText(), answer.getCorrect());
                                }
                            } else {
                                // For fill in blank, just set the answer text
                                if (!question.getAnswers().isEmpty()) {
                                    lastQv.setFillInBlankAnswer(question.getAnswers().get(0).getAnswerText());
                                }
                            }
                        }
                        
                        btnCreateQuiz.setText("Cập nhật Quiz");
                    }
                });
    }

    private void loadAIGeneratedQuiz() {
        String quizDataJson = getIntent().getStringExtra("quizData");
        if (quizDataJson == null) {
            Toast.makeText(this, "Lỗi: Không có dữ liệu quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            Gson gson = new Gson();
            Map<String, Object> quizMap = gson.fromJson(quizDataJson, Map.class);
            Quiz quiz = Quiz.fromMap(quizMap);
            
            // Set quiz title
            etQuizTitle.setText(quiz.getQuizTitle());
            
            // Clear default question if any
            questionsContainer.removeAllViews();
            questionViews.clear();
            
            // Load AI-generated questions
            for (Question question : quiz.getQuestions()) {
                Question.QuestionType type = question.getQuestionType() != null ? 
                        question.getQuestionType() : Question.QuestionType.SINGLE_CHOICE;
                addQuestionCard(type);
                QuestionView lastQv = questionViews.get(questionViews.size() - 1);
                lastQv.setQuestionText(question.getQuestionText());
                
                if (type != Question.QuestionType.FILL_IN_BLANK) {
                    lastQv.clearAnswers();
                    for (Answer answer : question.getAnswers()) {
                        lastQv.addAnswerField(answer.getAnswerText(), answer.getCorrect());
                    }
                } else {
                    // For fill in blank, just set the answer text
                    if (!question.getAnswers().isEmpty()) {
                        lastQv.setFillInBlankAnswer(question.getAnswers().get(0).getAnswerText());
                    }
                }
            }
            
            // Change button text to "Lưu Quiz"
            btnCreateQuiz.setText("Lưu Quiz");
            
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi parse dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveAIGeneratedQuiz() {
        String title = etQuizTitle.getText() != null ? etQuizTitle.getText().toString().trim() : "";
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Question> questions = collectQuestions();
        if (questions.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất 1 câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
        Quiz quiz = new Quiz(title, userId);
        quiz.setQuestions(questions);

        // Show saving progress
        btnCreateQuiz.setEnabled(false);
        btnCreateQuiz.setText("Đang lưu...");

        db.collection("quizzes")
                .add(quiz.toMap())
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Lưu quiz thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCreateQuiz.setEnabled(true);
                    btnCreateQuiz.setText("Lưu Quiz");
                });
    }

    // Inner class to manage question card views
    private class QuestionView {
        private View cardView;
        private int questionNumber;
        private Question.QuestionType questionType;
        private TabLayout tabQuestionType;
        private TextInputEditText etQuestion;
        private LinearLayout answersContainer;
        private Button btnAddAnswer;
        private ImageButton btnDeleteQuestion;
        private TextInputEditText etFillInBlankAnswer; // For fill in blank
        private List<AnswerView> answerViews = new ArrayList<>();

        public QuestionView(View cardView, int questionNumber, Question.QuestionType questionType) {
            this.cardView = cardView;
            this.questionNumber = questionNumber;
            this.questionType = questionType;
            
            // Set question title to "Câu hỏi" only
            ((android.widget.TextView) cardView.findViewById(R.id.tvQuestionNumber)).setText("Câu hỏi");
            
            etQuestion = cardView.findViewById(R.id.etQuestion);
            tabQuestionType = cardView.findViewById(R.id.tabQuestionType);
            btnDeleteQuestion = cardView.findViewById(R.id.btnDeleteQuestion);
            
            // Set initial tab selection
            if (tabQuestionType != null) {
                TabLayout.Tab tab = tabQuestionType.getTabAt(getTabIndexForType(questionType));
                if (tab != null) {
                    tab.select();
                }
            }
            
            // For fill in blank layout
            if (questionType == Question.QuestionType.FILL_IN_BLANK) {
                etFillInBlankAnswer = cardView.findViewById(R.id.etAnswer);
            } else {
                // For other layouts
                answersContainer = cardView.findViewById(R.id.answersContainer);
                btnAddAnswer = cardView.findViewById(R.id.btnAddAnswer);
                
                if (btnAddAnswer != null) {
                    btnAddAnswer.setOnClickListener(v -> addAnswerField());
                }
            }
            
            // Tab change listener
            if (tabQuestionType != null) {
                tabQuestionType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        Question.QuestionType newType = getTypeForTabIndex(tab.getPosition());
                        if (newType != QuestionView.this.questionType) {
                            switchQuestionType(newType);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });
            }
            
            btnDeleteQuestion.setOnClickListener(v -> {
                questionsContainer.removeView(cardView);
                questionViews.remove(this);
                updateQuestionNumbers();
            });
        }
        
        private int getTabIndexForType(Question.QuestionType type) {
            switch (type) {
                case SINGLE_CHOICE: return 0;
                case MULTIPLE_CHOICE: return 1;
                case FILL_IN_BLANK: return 2;
                default: return 0;
            }
        }
        
        private Question.QuestionType getTypeForTabIndex(int index) {
            switch (index) {
                case 0: return Question.QuestionType.SINGLE_CHOICE;
                case 1: return Question.QuestionType.MULTIPLE_CHOICE;
                case 2: return Question.QuestionType.FILL_IN_BLANK;
                default: return Question.QuestionType.SINGLE_CHOICE;
            }
        }
        
        private void switchQuestionType(Question.QuestionType newType) {
            // Save current question text
            String questionText = getQuestionText();
            
            // Get position in container
            int position = questionsContainer.indexOfChild(cardView);
            
            // Remove current view
            questionsContainer.removeView(cardView);
            questionViews.remove(this);
            
            // Create new question card with new type
            View newQuestionCard;
            if (newType == Question.QuestionType.FILL_IN_BLANK) {
                newQuestionCard = LayoutInflater.from(CreateQuizActivity.this)
                        .inflate(R.layout.item_question_fill_blank, questionsContainer, false);
            } else {
                newQuestionCard = LayoutInflater.from(CreateQuizActivity.this)
                        .inflate(R.layout.item_question_edit, questionsContainer, false);
            }
            
            // Create new QuestionView
            QuestionView newQuestionView = new QuestionView(newQuestionCard, questionNumber, newType);
            newQuestionView.setQuestionText(questionText);
            
            // Add default answers for non-fill-in-blank types
            if (newType != Question.QuestionType.FILL_IN_BLANK) {
                for (int i = 0; i < 4; i++) {
                    newQuestionView.addAnswerField();
                }
            }
            
            // Insert at same position
            questionViews.add(position, newQuestionView);
            questionsContainer.addView(newQuestionCard, position);
        }
        
        public Question.QuestionType getQuestionType() {
            return questionType;
        }

        public void addAnswerField() {
            addAnswerField("", false);
        }

        public void addAnswerField(String text, boolean isCorrect) {
            if (questionType == Question.QuestionType.FILL_IN_BLANK) {
                // Don't add answer fields for fill in blank
                return;
            }
            
            View answerView = LayoutInflater.from(CreateQuizActivity.this)
                    .inflate(R.layout.item_answer_edit, answersContainer, false);
            
            AnswerView av = new AnswerView(answerView, text, isCorrect, this);
            answerViews.add(av);
            answersContainer.addView(answerView);
        }

        public void clearAnswers() {
            if (answersContainer != null) {
                answersContainer.removeAllViews();
                answerViews.clear();
            }
        }
        
        // Remove answer from list
        public void removeAnswer(AnswerView answerView) {
            answerViews.remove(answerView);
            if (answersContainer != null) {
                answersContainer.removeView(answerView.getView());
            }
        }
        
        // Handle selection based on question type
        public void onAnswerSelected(AnswerView selectedAnswer, boolean isChecked) {
            if (questionType == Question.QuestionType.SINGLE_CHOICE) {
                // Single Choice: Only one can be selected
                for (AnswerView av : answerViews) {
                    if (av != selectedAnswer) {
                        av.setChecked(false);
                    }
                }
            } else if (questionType == Question.QuestionType.MULTIPLE_CHOICE) {
                // Multiple Response: Allow multiple selections
                // No special handling needed, just keep the current state
                // The checkbox will handle its own toggle behavior
            }
        }

        public String getQuestionText() {
            return etQuestion.getText() != null ? etQuestion.getText().toString().trim() : "";
        }

        public void setQuestionText(String text) {
            etQuestion.setText(text);
        }
        
        public void setFillInBlankAnswer(String text) {
            if (etFillInBlankAnswer != null) {
                etFillInBlankAnswer.setText(text);
            }
        }

        public List<Answer> getAnswers() {
            List<Answer> answers = new ArrayList<>();
            
            if (questionType == Question.QuestionType.FILL_IN_BLANK) {
                // For fill in blank, get the single answer
                if (etFillInBlankAnswer != null) {
                    String answerText = etFillInBlankAnswer.getText() != null ? 
                            etFillInBlankAnswer.getText().toString().trim() : "";
                    if (!answerText.isEmpty()) {
                        answers.add(new Answer(answerText, true));
                    }
                }
            } else {
                // For other types, get answers from answer views
                for (AnswerView av : answerViews) {
                    String answerText = av.getAnswerText();
                    if (!answerText.isEmpty()) {
                        answers.add(new Answer(answerText, av.isCorrect()));
                    }
                }
            }
            
            return answers;
        }
    }

    private class AnswerView {
        private View view;
        private TextInputEditText etAnswer;
        private RadioButton radioButton;
        private CheckBox checkBox;
        private ImageButton btnDeleteAnswer;
        private QuestionView parentQuestion;
        private long lastClickTime = 0;
        private static final int DOUBLE_CLICK_TIME_DELTA = 300; // milliseconds

        public AnswerView(View view, String text, boolean isCorrect, QuestionView parentQuestion) {
            this.view = view;
            this.parentQuestion = parentQuestion;
            
            etAnswer = view.findViewById(R.id.etAnswer);
            radioButton = view.findViewById(R.id.rbCorrect);
            checkBox = view.findViewById(R.id.cbCorrect);
            btnDeleteAnswer = view.findViewById(R.id.btnDeleteAnswer);
            
            etAnswer.setText(text);
            
            // Set up the correct control based on question type
            setupCorrectnessControl(isCorrect);
            
            // Handle delete button
            btnDeleteAnswer.setOnClickListener(v -> {
                parentQuestion.removeAnswer(this);
            });
        }
        
        private void setupCorrectnessControl(boolean isCorrect) {
            if (parentQuestion.getQuestionType() == Question.QuestionType.SINGLE_CHOICE) {
                // Use RadioButton for Single Choice
                radioButton.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.GONE);
                radioButton.setChecked(isCorrect);
                
                radioButton.setOnClickListener(v -> {
                    boolean isChecked = radioButton.isChecked();
                    if (isChecked) {
                        parentQuestion.onAnswerSelected(this, true);
                    }
                });
            } else if (parentQuestion.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE) {
                // Use CheckBox for Multiple Response
                radioButton.setVisibility(View.GONE);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(isCorrect);
                
                // Handle double-click to toggle checkbox
                checkBox.setOnClickListener(v -> {
                    long clickTime = System.currentTimeMillis();
                    if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                        // Double click detected - toggle the checkbox
                        checkBox.setChecked(!checkBox.isChecked());
                    }
                    lastClickTime = clickTime;
                    
                    // Notify parent about the selection change
                    parentQuestion.onAnswerSelected(this, checkBox.isChecked());
                });
            }
        }

        public View getView() {
            return view;
        }

        public String getAnswerText() {
            return etAnswer.getText() != null ? etAnswer.getText().toString().trim() : "";
        }

        public boolean isCorrect() {
            if (parentQuestion.getQuestionType() == Question.QuestionType.SINGLE_CHOICE) {
                return radioButton.isChecked();
            } else {
                return checkBox.isChecked();
            }
        }
        
        public void setChecked(boolean checked) {
            if (parentQuestion.getQuestionType() == Question.QuestionType.SINGLE_CHOICE) {
                radioButton.setChecked(checked);
            } else {
                checkBox.setChecked(checked);
            }
        }
    }

    private void updateQuestionNumbers() {
        for (int i = 0; i < questionViews.size(); i++) {
            QuestionView qv = questionViews.get(i);
            qv.questionNumber = i + 1;
            // Keep "Câu hỏi" only, no number
            ((android.widget.TextView) qv.cardView.findViewById(R.id.tvQuestionNumber))
                    .setText("Câu hỏi");
        }
    }
}

