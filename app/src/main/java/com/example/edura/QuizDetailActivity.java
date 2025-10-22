package com.example.edura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.adapter.QuestionAdapter;
import com.example.edura.model.Question;
import com.example.edura.model.Quiz;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvQuizTitle;
    private TextView tvQuestionCount;
    private TextView tvCreatedDate;
    private RecyclerView rvQuestions;
    private LinearLayout emptyState;
    private Button btnStartTest;

    private QuestionAdapter questionAdapter;
    private FirebaseFirestore db;
    private String quizId;
    private Quiz currentQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);

        db = FirebaseFirestore.getInstance();
        quizId = getIntent().getStringExtra("quizId");

        if (quizId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy Quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        loadQuizData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        rvQuestions = findViewById(R.id.rvQuestions);
        emptyState = findViewById(R.id.emptyState);
        btnStartTest = findViewById(R.id.btnStartTest);
    }

    private void setupRecyclerView() {
        questionAdapter = new QuestionAdapter(null); // Read-only mode, no delete listener
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(questionAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnStartTest.setOnClickListener(v -> {
            if (currentQuiz == null || currentQuiz.getQuestions() == null || currentQuiz.getQuestions().isEmpty()) {
                Toast.makeText(this, "Quiz chưa có câu hỏi nào", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to Test Config Activity
            Intent intent = new Intent(this, TestConfigActivity.class);
            intent.putExtra("quizId", quizId);
            intent.putExtra("quizTitle", currentQuiz.getQuizTitle());
            intent.putExtra("totalQuestions", currentQuiz.getQuestions().size());
            startActivity(intent);
        });
    }

    private void loadQuizData() {
        db.collection("quizzes")
                .document(quizId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentQuiz = documentSnapshot.toObject(Quiz.class);
                        if (currentQuiz != null) {
                            displayQuizData();
                        }
                    } else {
                        Toast.makeText(this, "Quiz không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayQuizData() {
        // Set title
        tvQuizTitle.setText(currentQuiz.getQuizTitle());

        // Set question count
        List<Question> questions = currentQuiz.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            tvQuestionCount.setText(questions.size() + " câu hỏi");
            questionAdapter.setQuestionList(questions);
            rvQuestions.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        } else {
            tvQuestionCount.setText("0 câu hỏi");
            rvQuestions.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }

        // Set created date
        if (currentQuiz.getCreatedAt() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateStr = sdf.format(new Date(currentQuiz.getCreatedAt()));
            tvCreatedDate.setText("Tạo: " + dateStr);
        } else {
            tvCreatedDate.setText("Tạo: N/A");
        }
    }
}

