package com.example.edura;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class TestConfigActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvQuizTitle;
    private TextView tvQuestionCount, tvQuestionNumber;
    private TextView tvTimeLimit, tvTimeNumber;
    private ImageButton btnDecreaseQuestions, btnIncreaseQuestions;
    private ImageButton btnDecreaseTime, btnIncreaseTime;
    private SwitchCompat switchShowAnswer;
    private RadioGroup radioGroupOrder;
    private Button btnStartTest;

    private String quizId;
    private String quizTitle;
    private int totalQuestions;
    private int selectedQuestions = 10;
    private int timeLimit = 10; // minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_config);

        // Get data from intent
        quizId = getIntent().getStringExtra("quizId");
        quizTitle = getIntent().getStringExtra("quizTitle");
        totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        if (quizId == null || totalQuestions == 0) {
            Toast.makeText(this, "Lỗi: Không có dữ liệu quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set initial selected questions to total or 10, whichever is smaller
        selectedQuestions = Math.min(totalQuestions, 100);
        // 1 minute per question initially
        timeLimit = selectedQuestions;

        initViews();
        setupListeners();
        updateUI();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvTimeLimit = findViewById(R.id.tvTimeLimit);
        tvTimeNumber = findViewById(R.id.tvTimeNumber);
        btnDecreaseQuestions = findViewById(R.id.btnDecreaseQuestions);
        btnIncreaseQuestions = findViewById(R.id.btnIncreaseQuestions);
        btnDecreaseTime = findViewById(R.id.btnDecreaseTime);
        btnIncreaseTime = findViewById(R.id.btnIncreaseTime);
        switchShowAnswer = findViewById(R.id.switchShowAnswer);
        radioGroupOrder = findViewById(R.id.radioGroupOrder);
        btnStartTest = findViewById(R.id.btnStartTest);

        tvQuizTitle.setText(quizTitle);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDecreaseQuestions.setOnClickListener(v -> {
            if (selectedQuestions > 1) {
                selectedQuestions--;
                updateUI();
            }
        });

        btnIncreaseQuestions.setOnClickListener(v -> {
            if (selectedQuestions < totalQuestions) {
                selectedQuestions++;
                updateUI();
            }
        });

        btnDecreaseTime.setOnClickListener(v -> {
            if (timeLimit > 1) {
                timeLimit--;
                updateUI();
            }
        });

        btnIncreaseTime.setOnClickListener(v -> {
            if (timeLimit < 120) { // max 120 minutes
                timeLimit++;
                updateUI();
            }
        });

        btnStartTest.setOnClickListener(v -> startTest());
    }

    private void updateUI() {
        tvQuestionNumber.setText(String.valueOf(selectedQuestions));
        tvQuestionCount.setText(selectedQuestions + " / " + totalQuestions + " câu");

        tvTimeNumber.setText(String.valueOf(timeLimit));
        tvTimeLimit.setText(timeLimit + " phút");
    }

    private void startTest() {
        boolean showAnswer = switchShowAnswer.isChecked();
        boolean isRandom = radioGroupOrder.getCheckedRadioButtonId() == R.id.radioRandom;

        Intent intent = new Intent(this, TakeTestActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("quizTitle", quizTitle);
        intent.putExtra("questionCount", selectedQuestions);
        intent.putExtra("timeLimit", timeLimit);
        intent.putExtra("showAnswer", showAnswer);
        intent.putExtra("isRandom", isRandom);
        
        startActivity(intent);
    }
}

