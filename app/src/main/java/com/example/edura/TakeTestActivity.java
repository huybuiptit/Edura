package com.example.edura;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.edura.adapter.TestQuestionAdapter;
import com.example.edura.model.Answer;
import com.example.edura.model.Question;
import com.example.edura.model.Quiz;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TakeTestActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvQuizTitle, tvTimer;
    private ProgressBar progressBar;
    private ViewPager2 viewPagerQuestions;
    private Button btnPrevious, btnNext, btnSubmit;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String quizId, quizTitle;
    private int questionCount, timeLimit;
    private boolean showAnswer, isRandom;

    private List<Question> questions = new ArrayList<>();
    private TestQuestionAdapter adapter;
    private CountDownTimer timer;
    private long startTime;
    private long elapsedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_test);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        getExtras();
        initViews();
        loadQuestions();
    }

    private void getExtras() {
        Intent intent = getIntent();
        quizId = intent.getStringExtra("quizId");
        quizTitle = intent.getStringExtra("quizTitle");
        questionCount = intent.getIntExtra("questionCount", 10);
        timeLimit = intent.getIntExtra("timeLimit", 10);
        showAnswer = intent.getBooleanExtra("showAnswer", true);
        isRandom = intent.getBooleanExtra("isRandom", true);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvTimer = findViewById(R.id.tvTimer);
        progressBar = findViewById(R.id.progressBar);
        viewPagerQuestions = findViewById(R.id.viewPagerQuestions);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);

        tvQuizTitle.setText(quizTitle);

        btnBack.setOnClickListener(v -> confirmExit());
        btnPrevious.setOnClickListener(v -> previousQuestion());
        btnNext.setOnClickListener(v -> nextQuestion());
        btnSubmit.setOnClickListener(v -> submitTest());

        viewPagerQuestions.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateNavigationButtons(position);
                updateProgress(position);
            }
        });
    }

    private void loadQuestions() {
        db.collection("quizzes")
                .document(quizId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Quiz quiz = documentSnapshot.toObject(Quiz.class);
                        if (quiz != null && quiz.getQuestions() != null) {
                            List<Question> allQuestions = quiz.getQuestions();
                            
                            // Shuffle if random
                            if (isRandom) {
                                Collections.shuffle(allQuestions);
                            }
                            
                            // Take only questionCount questions
                            questions = allQuestions.size() > questionCount
                                    ? allQuestions.subList(0, questionCount)
                                    : allQuestions;
                            
                            setupViewPager();
                            startTimer();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải câu hỏi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void setupViewPager() {
        // Debug: Log all questions and answers
        Log.d("TakeTest", "=== Loading " + questions.size() + " questions ===");
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Log.d("TakeTest", "Q" + i + ": " + q.getQuestionText());
            for (int j = 0; j < q.getAnswers().size(); j++) {
                Answer a = q.getAnswers().get(j);
                Log.d("TakeTest", "  A" + j + ": " + a.getAnswerText() + " - correct: " + a.getCorrect());
            }
        }
        
        adapter = new TestQuestionAdapter(questions, showAnswer);
        viewPagerQuestions.setAdapter(adapter);
        progressBar.setMax(questions.size());
        updateProgress(0);
        updateNavigationButtons(0);
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new CountDownTimer(timeLimit * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvTimer.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0:00");
                Toast.makeText(TakeTestActivity.this, "Hết giờ!", Toast.LENGTH_SHORT).show();
                submitTest();
            }
        }.start();
    }

    private void previousQuestion() {
        int currentPosition = viewPagerQuestions.getCurrentItem();
        if (currentPosition > 0) {
            viewPagerQuestions.setCurrentItem(currentPosition - 1);
        }
    }

    private void nextQuestion() {
        int currentPosition = viewPagerQuestions.getCurrentItem();
        if (currentPosition < questions.size() - 1) {
            viewPagerQuestions.setCurrentItem(currentPosition + 1);
        }
    }

    private void updateNavigationButtons(int position) {
        btnPrevious.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        
        if (position == questions.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }
    }

    private void updateProgress(int position) {
        progressBar.setProgress(position + 1);
    }

    private void submitTest() {
        if (timer != null) {
            timer.cancel();
        }
        
        elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        
        // Get results from adapter
        int correctAnswers = adapter != null ? adapter.getCorrectAnswersCount() : 0;
        int questionsCompleted = adapter != null ? adapter.getAnsweredQuestionsCount() : 0;
        double totalScore = adapter != null ? adapter.getTotalScore() : 0.0;
        
        // Calculate score percentage based on total score
        int scorePercentage = questions.size() > 0 ? (int) Math.round((totalScore / questions.size()) * 100) : 0;
        
        // Debug log
        android.util.Log.d("TakeTest", "Questions: " + questions.size() + 
                ", Correct: " + correctAnswers + 
                ", Completed: " + questionsCompleted + 
                ", Total Score: " + totalScore +
                ", Score %: " + scorePercentage + 
                ", Time: " + elapsedTime + "s");
        
        Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("quizTitle", quizTitle);
        intent.putExtra("totalQuestions", questions.size());
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("questionsCompleted", questionsCompleted);
        intent.putExtra("completionTime", elapsedTime);
        intent.putExtra("scorePercentage", scorePercentage);
        intent.putExtra("totalScore", totalScore);
        startActivity(intent);
        finish();
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát bài kiểm tra?")
                .setMessage("Bạn có chắc muốn thoát? Kết quả sẽ được lưu lại.")
                .setPositiveButton("Thoát", (dialog, which) -> submitTest())
                .setNegativeButton("Tiếp tục", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
