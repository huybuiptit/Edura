package com.example.edura;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.edura.model.AIQuizzRequest;
import com.example.edura.model.AIQuizzRespond;
import com.example.edura.model.Quiz;
import com.example.edura.service.AIQuizService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class CreateAIQuizActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etContext;
    private ImageButton btnDecrement, btnIncrement;
    private TextView tvQuestionCount;
    private AutoCompleteTextView spinnerLanguage, spinnerQuestionType;
    private MaterialButton btnEasy, btnMedium, btnDifficult;
    private Button btnGenerateQuiz;
    private FrameLayout loadingOverlay;

    private int questionCount = 5;
    private String selectedDifficulty = "Easy"; // Default value
    private String selectedLanguage = "Tiếng Việt";
    private String selectedQuestionType = "Single Choice";
    
    private AIQuizService aiQuizService;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ai_quiz);

        aiQuizService = new AIQuizService();
        auth = FirebaseAuth.getInstance();

        initViews();
        setupLanguageSpinner();
        setupQuestionTypeSpinner();
        setupDifficultyButtons();
        setupListeners();
        
        // Set default difficulty UI after all views initialized
        updateDifficultyButtons(btnEasy);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etContext = findViewById(R.id.etContext);
        btnDecrement = findViewById(R.id.btnDecrement);
        btnIncrement = findViewById(R.id.btnIncrement);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        spinnerQuestionType = findViewById(R.id.spinnerQuestionType);
        btnEasy = findViewById(R.id.btnEasy);
        btnMedium = findViewById(R.id.btnMedium);
        btnDifficult = findViewById(R.id.btnDifficult);
        btnGenerateQuiz = findViewById(R.id.btnGenerateQuiz);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        
        // Enable context input
        if (etContext != null) {
            etContext.setFocusable(true);
            etContext.setFocusableInTouchMode(true);
            etContext.setClickable(true);
        }
    }

    private void setupLanguageSpinner() {
        String[] languages = new String[]{"Tiếng Việt", "English", "中文", "日本語", "한국어"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        spinnerLanguage.setAdapter(adapter);
        spinnerLanguage.setText(languages[0], false);

        spinnerLanguage.setOnItemClickListener((parent, view, position, id) -> {
            selectedLanguage = languages[position];
        });
    }

    private void setupQuestionTypeSpinner() {
        String[] questionTypes = new String[]{"Trắc nghiệm 1 đáp án", "Trắc nghiệm nhiều đáp án", "Điền vào chỗ trống"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, questionTypes);
        spinnerQuestionType.setAdapter(adapter);
        spinnerQuestionType.setText(questionTypes[0], false);

        spinnerQuestionType.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    selectedQuestionType = "Single Choice";
                    break;
                case 1:
                    selectedQuestionType = "Multiple Choice";
                    break;
                case 2:
                    selectedQuestionType = "Fill in Blank";
                    break;
            }
        });
    }

    private void setupDifficultyButtons() {
        btnEasy.setOnClickListener(v -> {
            selectedDifficulty = "Easy";
            updateDifficultyButtons(btnEasy);
        });

        btnMedium.setOnClickListener(v -> {
            selectedDifficulty = "Medium";
            updateDifficultyButtons(btnMedium);
        });

        btnDifficult.setOnClickListener(v -> {
            selectedDifficulty = "Difficult";
            updateDifficultyButtons(btnDifficult);
        });
    }

    private void updateDifficultyButtons(MaterialButton selectedButton) {
        // Reset all buttons
        btnEasy.setStrokeColorResource(R.color.border_gray);
        btnEasy.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
        btnEasy.setTextColor(ContextCompat.getColor(this, R.color.dark_text));

        btnMedium.setStrokeColorResource(R.color.border_gray);
        btnMedium.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
        btnMedium.setTextColor(ContextCompat.getColor(this, R.color.dark_text));

        btnDifficult.setStrokeColorResource(R.color.border_gray);
        btnDifficult.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
        btnDifficult.setTextColor(ContextCompat.getColor(this, R.color.dark_text));

        // Highlight selected button
        selectedButton.setStrokeColorResource(R.color.nav_selected);
        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_blue_bg));
        selectedButton.setTextColor(ContextCompat.getColor(this, R.color.nav_selected));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDecrement.setOnClickListener(v -> {
            if (questionCount > 1) {
                questionCount--;
                tvQuestionCount.setText(String.valueOf(questionCount));
            }
        });

        btnIncrement.setOnClickListener(v -> {
            if (questionCount < 20) {
                questionCount++;
                tvQuestionCount.setText(String.valueOf(questionCount));
            }
        });

        btnGenerateQuiz.setOnClickListener(v -> generateQuiz());
    }

    private void generateQuiz() {
        String context = etContext.getText() != null ? etContext.getText().toString().trim() : "";

        // Debug log
        android.util.Log.d("CreateAIQuiz", "Context: " + context);
        android.util.Log.d("CreateAIQuiz", "Difficulty: " + selectedDifficulty);
        android.util.Log.d("CreateAIQuiz", "Language: " + selectedLanguage);
        android.util.Log.d("CreateAIQuiz", "QuestionType: " + selectedQuestionType);
        android.util.Log.d("CreateAIQuiz", "Count: " + questionCount);

        // Create request object
        AIQuizzRequest request = new AIQuizzRequest(
                context,
                selectedQuestionType,
                selectedLanguage,
                selectedDifficulty,
                questionCount
        );

        // Validate request
        String validationError = request.getValidationError();
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_LONG).show();
            android.util.Log.e("CreateAIQuiz", "Validation error: " + validationError);
            return;
        }

        // Show loading overlay
        showLoading();

        // Call AI API
        aiQuizService.generateQuiz(request, new AIQuizService.AIQuizCallback() {
            @Override
            public void onSuccess(AIQuizzRespond response) {
                android.util.Log.d("CreateAIQuiz", "API Success - Converting to Quiz");
                
                try {
                    hideLoading();
                    
                    // Validate response has questions
                    if (response.getQuestions() == null || response.getQuestions().isEmpty()) {
                        android.util.Log.e("CreateAIQuiz", "Response has no questions!");
                        Toast.makeText(CreateAIQuizActivity.this, 
                            "Lỗi: Backend không trả về câu hỏi nào. Vui lòng thử lại.", 
                            Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    android.util.Log.d("CreateAIQuiz", "Got " + response.getQuestions().size() + " questions");
                    
                    // Convert response to Quiz model
                    String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                    Quiz quiz = response.toQuiz(userId);
                    
                    android.util.Log.d("CreateAIQuiz", "Quiz title: " + quiz.getQuizTitle());
                    android.util.Log.d("CreateAIQuiz", "Quiz questions: " + quiz.getQuestions().size());
                    
                    // Navigate to CreateQuizActivity in AI edit mode
                    Intent intent = new Intent(CreateAIQuizActivity.this, CreateQuizActivity.class);
                    intent.putExtra("aiGeneratedQuiz", true);
                    intent.putExtra("quizData", new Gson().toJson(quiz.toMap()));
                    startActivity(intent);
                    finish();
                    
                } catch (Exception e) {
                    android.util.Log.e("CreateAIQuiz", "Error processing response: " + e.getMessage(), e);
                    Toast.makeText(CreateAIQuizActivity.this, 
                        "Lỗi xử lý dữ liệu: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("CreateAIQuiz", "API Error: " + error);
                hideLoading();
                Toast.makeText(CreateAIQuizActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        btnGenerateQuiz.setEnabled(false);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        btnGenerateQuiz.setEnabled(true);
    }
}

