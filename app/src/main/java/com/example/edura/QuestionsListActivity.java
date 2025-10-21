package com.example.edura;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.adapter.QuestionAdapter;
import com.example.edura.model.Answer;
import com.example.edura.model.Question;
import com.example.edura.model.Quiz;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuestionsListActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionClickListener {

    private RecyclerView rvQuestions;
    private QuestionAdapter questionAdapter;
    private TextView tvQuizTitle, tvQuestionCount;
    private ImageButton btnBack, btnEditQuiz;
    private View emptyState;
    private FloatingActionButton fabAddQuestion;

    private FirebaseFirestore db;
    private String quizId;
    private Quiz currentQuiz;
    private List<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_list);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get quiz data from intent
        quizId = getIntent().getStringExtra("quizId");
        String quizTitle = getIntent().getStringExtra("quizTitle");
        boolean openAddQuestion = getIntent().getBooleanExtra("openAddQuestion", false);

        // Initialize views
        initViews();
        
        // Set quiz title
        tvQuizTitle.setText(quizTitle);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Load quiz and questions
        loadQuiz();
        
        // Setup listeners
        setupListeners();

        // Open add question dialog if requested
        if (openAddQuestion) {
            fabAddQuestion.postDelayed(() -> showCreateQuestionDialog(), 300);
        }
    }

    private void initViews() {
        rvQuestions = findViewById(R.id.rvQuestions);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        btnBack = findViewById(R.id.btnBack);
        btnEditQuiz = findViewById(R.id.btnEditQuiz);
        emptyState = findViewById(R.id.emptyState);
        
        // Add FAB programmatically
        fabAddQuestion = new FloatingActionButton(this);
        fabAddQuestion.setImageResource(android.R.drawable.ic_input_add);
        fabAddQuestion.setBackgroundTintList(getColorStateList(R.color.light_blue));
        fabAddQuestion.setImageTintList(getColorStateList(R.color.white));
    }

    private void setupRecyclerView() {
        questionAdapter = new QuestionAdapter(this);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(questionAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnEditQuiz.setOnClickListener(v -> {
            // Edit quiz title
            showEditQuizDialog();
        });
        
        // Add FAB to layout
        findViewById(android.R.id.content).post(() -> {
            View rootView = findViewById(android.R.id.content);
            if (rootView instanceof android.view.ViewGroup) {
                android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END;
                params.setMargins(0, 0, dpToPx(16), dpToPx(16));
                fabAddQuestion.setLayoutParams(params);
                ((android.view.ViewGroup) rootView).addView(fabAddQuestion);
                
                fabAddQuestion.setOnClickListener(v -> showCreateQuestionDialog());
            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void loadQuiz() {
        db.collection("quizzes")
                .document(quizId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading quiz: " + error.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentQuiz = Quiz.fromMap(documentSnapshot.getData());
                        currentQuiz.setQuizId(documentSnapshot.getId());
                        
                        questionList = currentQuiz.getQuestions();
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        questionAdapter.setQuestionList(questionList);
        
        int count = questionList.size();
        tvQuestionCount.setText(count + (count == 1 ? " question" : " questions"));
        
        if (questionList.isEmpty()) {
            rvQuestions.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvQuestions.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showEditQuizDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_quiz, null);
        builder.setView(dialogView);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etQuizTitle = dialogView.findViewById(R.id.etQuizTitle);

        tvDialogTitle.setText(R.string.edit_quiz);
        etQuizTitle.setText(currentQuiz.getQuizTitle());

        AlertDialog dialog = builder.create();
        
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String title = etQuizTitle.getText() != null ? etQuizTitle.getText().toString().trim() : "";
            
            if (title.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_title, Toast.LENGTH_SHORT).show();
                return;
            }

            updateQuizTitle(title);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateQuizTitle(String newTitle) {
        db.collection("quizzes")
                .document(quizId)
                .update("quizTitle", newTitle, "updatedAt", System.currentTimeMillis())
                .addOnSuccessListener(aVoid -> {
                    tvQuizTitle.setText(newTitle);
                    Toast.makeText(this, R.string.quiz_updated, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showCreateQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_question, null);
        builder.setView(dialogView);

        TextInputEditText etQuestionText = dialogView.findViewById(R.id.etQuestionText);
        RadioGroup rgQuestionType = dialogView.findViewById(R.id.rgQuestionType);
        TextInputEditText etAnswer1 = dialogView.findViewById(R.id.etAnswer1);
        TextInputEditText etAnswer2 = dialogView.findViewById(R.id.etAnswer2);
        TextInputEditText etAnswer3 = dialogView.findViewById(R.id.etAnswer3);
        TextInputEditText etAnswer4 = dialogView.findViewById(R.id.etAnswer4);
        CheckBox cbAnswer1 = dialogView.findViewById(R.id.cbAnswer1);
        CheckBox cbAnswer2 = dialogView.findViewById(R.id.cbAnswer2);
        CheckBox cbAnswer3 = dialogView.findViewById(R.id.cbAnswer3);
        CheckBox cbAnswer4 = dialogView.findViewById(R.id.cbAnswer4);
        RadioButton rbSingleChoice = dialogView.findViewById(R.id.rbSingleChoice);

        AlertDialog dialog = builder.create();
        
        // Handle single choice - only one checkbox can be checked
        rgQuestionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbSingleChoice) {
                // Single choice mode - make checkboxes behave like radio buttons
                View.OnClickListener singleChoiceListener = v -> {
                    cbAnswer1.setChecked(v == cbAnswer1);
                    cbAnswer2.setChecked(v == cbAnswer2);
                    cbAnswer3.setChecked(v == cbAnswer3);
                    cbAnswer4.setChecked(v == cbAnswer4);
                };
                cbAnswer1.setOnClickListener(singleChoiceListener);
                cbAnswer2.setOnClickListener(singleChoiceListener);
                cbAnswer3.setOnClickListener(singleChoiceListener);
                cbAnswer4.setOnClickListener(singleChoiceListener);
            } else {
                // Multiple choice mode - restore normal checkbox behavior
                cbAnswer1.setOnClickListener(null);
                cbAnswer2.setOnClickListener(null);
                cbAnswer3.setOnClickListener(null);
                cbAnswer4.setOnClickListener(null);
            }
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String questionText = etQuestionText.getText() != null ? 
                    etQuestionText.getText().toString().trim() : "";
            String answer1 = etAnswer1.getText() != null ? etAnswer1.getText().toString().trim() : "";
            String answer2 = etAnswer2.getText() != null ? etAnswer2.getText().toString().trim() : "";
            String answer3 = etAnswer3.getText() != null ? etAnswer3.getText().toString().trim() : "";
            String answer4 = etAnswer4.getText() != null ? etAnswer4.getText().toString().trim() : "";

            // Validation
            if (questionText.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_question, Toast.LENGTH_SHORT).show();
                return;
            }

            if (answer1.isEmpty() || answer2.isEmpty() || answer3.isEmpty() || answer4.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_answers, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbAnswer1.isChecked() && !cbAnswer2.isChecked() && 
                !cbAnswer3.isChecked() && !cbAnswer4.isChecked()) {
                Toast.makeText(this, R.string.error_no_correct_answer, Toast.LENGTH_SHORT).show();
                return;
            }

            // Create question
            Question.QuestionType type = rgQuestionType.getCheckedRadioButtonId() == R.id.rbSingleChoice ?
                    Question.QuestionType.SINGLE_CHOICE : Question.QuestionType.MULTIPLE_CHOICE;
            
            Question question = new Question(questionText, type);
            question.addAnswer(new Answer(answer1, cbAnswer1.isChecked()));
            question.addAnswer(new Answer(answer2, cbAnswer2.isChecked()));
            question.addAnswer(new Answer(answer3, cbAnswer3.isChecked()));
            question.addAnswer(new Answer(answer4, cbAnswer4.isChecked()));

            createQuestion(question);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void createQuestion(Question question) {
        // Add question to current quiz
        currentQuiz.addQuestion(question);

        // Update quiz in Firestore
        db.collection("quizzes")
                .document(quizId)
                .update("questions", currentQuiz.getQuestions().stream()
                        .map(Question::toMap)
                        .collect(java.util.stream.Collectors.toList()),
                        "updatedAt", System.currentTimeMillis())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, R.string.question_created, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteQuestion(Question question, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete_question)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    // Remove question from list
                    currentQuiz.getQuestions().remove(position);

                    // Update quiz in Firestore
                    db.collection("quizzes")
                            .document(quizId)
                            .update("questions", currentQuiz.getQuestions().stream()
                                    .map(Question::toMap)
                                    .collect(java.util.stream.Collectors.toList()),
                                    "updatedAt", System.currentTimeMillis())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, R.string.question_deleted, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}


