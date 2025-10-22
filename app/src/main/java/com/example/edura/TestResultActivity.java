package com.example.edura;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edura.model.TestResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class TestResultActivity extends AppCompatActivity {

    private ProgressBar circularProgress;
    private TextView tvScorePercentage, tvCompletionTime, tvQuestionsCompleted, tvCorrectAnswers;
    private Button btnRetry, btnConfirm;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String quizId, quizTitle;
    private int totalQuestions, correctAnswers, questionsCompleted;
    private long completionTime;
    private int scorePercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        getExtras();
        initViews();
        displayResults();
        setupListeners();
    }

    private void getExtras() {
        Intent intent = getIntent();
        quizId = intent.getStringExtra("quizId");
        quizTitle = intent.getStringExtra("quizTitle");
        totalQuestions = intent.getIntExtra("totalQuestions", 0);
        correctAnswers = intent.getIntExtra("correctAnswers", 0);
        questionsCompleted = intent.getIntExtra("questionsCompleted", 0);
        completionTime = intent.getLongExtra("completionTime", 0);

        // Use the calculated score percentage from TakeTestActivity
        scorePercentage = intent.getIntExtra("scorePercentage", 0);
        
        // Debug log
        android.util.Log.d("TestResult", "Total: " + totalQuestions + 
                ", Correct: " + correctAnswers + 
                ", Completed: " + questionsCompleted + 
                ", Score: " + scorePercentage + "%");
    }

    private void initViews() {
        circularProgress = findViewById(R.id.circularProgress);
        tvScorePercentage = findViewById(R.id.tvScorePercentage);
        tvCompletionTime = findViewById(R.id.tvCompletionTime);
        tvQuestionsCompleted = findViewById(R.id.tvQuestionsCompleted);
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        btnRetry = findViewById(R.id.btnRetry);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void displayResults() {
        // Set score percentage
        String scoreText = String.valueOf(scorePercentage) + "%";
        tvScorePercentage.setText(scoreText);
        circularProgress.setProgress(scorePercentage);

        // Set completion time
        long minutes = completionTime / 60;
        long seconds = completionTime % 60;
        String timeText = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        tvCompletionTime.setText(timeText);

        // Set questions completed
        String completedText = String.valueOf(questionsCompleted) + "/" + String.valueOf(totalQuestions);
        tvQuestionsCompleted.setText(completedText);

        // Set correct answers
        String correctText = String.valueOf(correctAnswers) + "/" + String.valueOf(totalQuestions);
        tvCorrectAnswers.setText(correctText);
        
        // Debug log để kiểm tra
        android.util.Log.d("TestResult", "Display - Score: " + scoreText + 
                ", Time: " + timeText + 
                ", Completed: " + completedText + 
                ", Correct: " + correctText);
    }

    private void setupListeners() {
        btnRetry.setOnClickListener(v -> retryTest());
        btnConfirm.setOnClickListener(v -> saveAndFinish());
    }

    private void retryTest() {
        // Go back to TestConfigActivity
        Intent intent = new Intent(this, TestConfigActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("quizTitle", quizTitle);
        intent.putExtra("totalQuestions", totalQuestions);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void saveAndFinish() {
        FirebaseUser user = auth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để lưu kết quả.", Toast.LENGTH_SHORT).show();
            goToHome();
            return;
        }

        String userId = user.getUid();
        TestResult result = new TestResult(
                quizId,
                quizTitle,
                userId,
                totalQuestions,
                correctAnswers,
                questionsCompleted,
                completionTime,
                scorePercentage  // Truyền scorePercentage đã tính với partial credit
        );

        db.collection("test_results")
                .add(result.toMap())
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đã lưu kết quả", Toast.LENGTH_SHORT).show();
                    goToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lưu kết quả: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    goToHome();
                });
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveAndFinish();
    }
}

