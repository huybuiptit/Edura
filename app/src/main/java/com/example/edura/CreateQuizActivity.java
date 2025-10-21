package com.example.edura;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edura.model.Answer;
import com.example.edura.model.Question;
import com.example.edura.model.Quiz;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TabLayout tabLayout;
    private TextInputEditText etQuizTitle;
    private LinearLayout questionsContainer;
    private Button btnAddQuestion, btnCreateQuiz;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    private List<QuestionView> questionViews = new ArrayList<>();
    private Question.QuestionType currentQuestionType = Question.QuestionType.SINGLE_CHOICE;
    
    private String quizIdToEdit = null; // For edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Check if editing existing quiz
        quizIdToEdit = getIntent().getStringExtra("quizId");
        
        initViews();
        setupListeners();
        
        // Add first default question
        addQuestionCard();
        
        // Load quiz data if editing
        if (quizIdToEdit != null) {
            loadQuizForEdit();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tabLayout = findViewById(R.id.tabLayout);
        etQuizTitle = findViewById(R.id.etQuizTitle);
        questionsContainer = findViewById(R.id.questionsContainer);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnCreateQuiz = findViewById(R.id.btnCreateQuiz);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentQuestionType = Question.QuestionType.SINGLE_CHOICE;
                        break;
                    case 1:
                        currentQuestionType = Question.QuestionType.MULTIPLE_CHOICE;
                        break;
                    case 2:
                        // TODO: Implement Fill in Blank
                        Toast.makeText(CreateQuizActivity.this, "Fill in Blank - Coming soon", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnAddQuestion.setOnClickListener(v -> addQuestionCard());

        btnCreateQuiz.setOnClickListener(v -> {
            if (quizIdToEdit != null) {
                updateQuiz();
            } else {
                createQuiz();
            }
        });
    }

    private void addQuestionCard() {
        View questionCard = LayoutInflater.from(this).inflate(R.layout.item_question_edit, questionsContainer, false);
        
        QuestionView questionView = new QuestionView(questionCard, questionViews.size() + 1);
        questionViews.add(questionView);
        
        questionsContainer.addView(questionCard);
        
        // Add 4 default answers
        for (int i = 0; i < 4; i++) {
            questionView.addAnswerField();
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
            
            Question question = new Question(questionText, currentQuestionType);
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
                            addQuestionCard();
                            QuestionView lastQv = questionViews.get(questionViews.size() - 1);
                            lastQv.setQuestionText(question.getQuestionText());
                            lastQv.clearAnswers();
                            for (Answer answer : question.getAnswers()) {
                                lastQv.addAnswerField(answer.getAnswerText(), answer.isCorrect());
                            }
                        }
                        
                        btnCreateQuiz.setText("Cập nhật Quiz");
                    }
                });
    }

    // Inner class to manage question card views
    private class QuestionView {
        private View cardView;
        private int questionNumber;
        private TextInputEditText etQuestion;
        private LinearLayout answersContainer;
        private Button btnAddAnswer;
        private ImageButton btnDeleteQuestion;
        private List<AnswerView> answerViews = new ArrayList<>();

        public QuestionView(View cardView, int questionNumber) {
            this.cardView = cardView;
            this.questionNumber = questionNumber;
            
            // Set question title to "Câu hỏi" only
            ((android.widget.TextView) cardView.findViewById(R.id.tvQuestionNumber)).setText("Câu hỏi");
            
            etQuestion = cardView.findViewById(R.id.etQuestion);
            answersContainer = cardView.findViewById(R.id.answersContainer);
            btnAddAnswer = cardView.findViewById(R.id.btnAddAnswer);
            btnDeleteQuestion = cardView.findViewById(R.id.btnDeleteQuestion);
            
            btnAddAnswer.setOnClickListener(v -> addAnswerField());
            
            btnDeleteQuestion.setOnClickListener(v -> {
                questionsContainer.removeView(cardView);
                questionViews.remove(this);
                updateQuestionNumbers();
            });
        }

        public void addAnswerField() {
            addAnswerField("", false);
        }

        public void addAnswerField(String text, boolean isCorrect) {
            View answerView = LayoutInflater.from(CreateQuizActivity.this)
                    .inflate(R.layout.item_answer_edit, answersContainer, false);
            
            AnswerView av = new AnswerView(answerView, text, isCorrect, this);
            answerViews.add(av);
            answersContainer.addView(answerView);
        }

        public void clearAnswers() {
            answersContainer.removeAllViews();
            answerViews.clear();
        }
        
        // Remove answer from list
        public void removeAnswer(AnswerView answerView) {
            answerViews.remove(answerView);
            answersContainer.removeView(answerView.getView());
        }
        
        // Handle selection based on question type
        public void onAnswerSelected(AnswerView selectedAnswer, boolean isChecked) {
            if (currentQuestionType == Question.QuestionType.SINGLE_CHOICE) {
                // Single Choice: Only one can be selected
                for (AnswerView av : answerViews) {
                    if (av != selectedAnswer) {
                        av.setChecked(false);
                    }
                }
            } else if (currentQuestionType == Question.QuestionType.MULTIPLE_CHOICE) {
                // Multiple Response: Toggle behavior - can select/unselect multiple
                // No special handling needed, just keep the current state
            }
        }

        public String getQuestionText() {
            return etQuestion.getText() != null ? etQuestion.getText().toString().trim() : "";
        }

        public void setQuestionText(String text) {
            etQuestion.setText(text);
        }

        public List<Answer> getAnswers() {
            List<Answer> answers = new ArrayList<>();
            for (AnswerView av : answerViews) {
                String answerText = av.getAnswerText();
                if (!answerText.isEmpty()) {
                    answers.add(new Answer(answerText, av.isCorrect()));
                }
            }
            return answers;
        }
    }

    private class AnswerView {
        private View view;
        private TextInputEditText etAnswer;
        private RadioButton radioButton;
        private ImageButton btnDeleteAnswer;
        private QuestionView parentQuestion;

        public AnswerView(View view, String text, boolean isCorrect, QuestionView parentQuestion) {
            this.view = view;
            this.parentQuestion = parentQuestion;
            
            etAnswer = view.findViewById(R.id.etAnswer);
            radioButton = view.findViewById(R.id.rbCorrect);
            btnDeleteAnswer = view.findViewById(R.id.btnDeleteAnswer);
            
            etAnswer.setText(text);
            radioButton.setChecked(isCorrect);
            
            // Handle radio button click with toggle for Multiple Response
            radioButton.setOnClickListener(v -> {
                boolean isChecked = radioButton.isChecked();
                
                if (currentQuestionType == Question.QuestionType.SINGLE_CHOICE) {
                    // Single Choice: always stays checked, uncheck others
                    if (isChecked) {
                        parentQuestion.onAnswerSelected(this, true);
                    }
                } else if (currentQuestionType == Question.QuestionType.MULTIPLE_CHOICE) {
                    // Multiple Response: toggle behavior
                    // If already checked and clicked again, uncheck it
                    parentQuestion.onAnswerSelected(this, isChecked);
                }
            });
            
            // Handle delete button
            btnDeleteAnswer.setOnClickListener(v -> {
                parentQuestion.removeAnswer(this);
            });
        }

        public View getView() {
            return view;
        }

        public String getAnswerText() {
            return etAnswer.getText() != null ? etAnswer.getText().toString().trim() : "";
        }

        public boolean isCorrect() {
            return radioButton.isChecked();
        }
        
        public void setChecked(boolean checked) {
            radioButton.setChecked(checked);
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

