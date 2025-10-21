package com.example.edura.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.R;
import com.example.edura.adapter.QuizAdapter;
import com.example.edura.model.Quiz;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment implements QuizAdapter.OnQuizClickListener {

    private RecyclerView rvQuizzes;
    private QuizAdapter quizAdapter;
    private View emptyState;
    private MaterialCardView cardAiQuiz, cardManualQuiz;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<Quiz> quizList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadQuizzes();
        
        return view;
    }

    private void initViews(View view) {
        rvQuizzes = view.findViewById(R.id.rvQuizzes);
        emptyState = view.findViewById(R.id.emptyState);
        cardAiQuiz = view.findViewById(R.id.cardAiQuiz);
        cardManualQuiz = view.findViewById(R.id.cardManualQuiz);
    }

    private void setupRecyclerView() {
        quizAdapter = new QuizAdapter(this);
        rvQuizzes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuizzes.setAdapter(quizAdapter);
    }

    private void setupListeners() {
        cardAiQuiz.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tạo câu hỏi với AI - Đang phát triển", Toast.LENGTH_SHORT).show();
        });
        
        cardManualQuiz.setOnClickListener(v -> {
            // Navigate to CreateQuizActivity
            Intent intent = new Intent(getContext(), com.example.edura.CreateQuizActivity.class);
            startActivity(intent);
        });
    }

    private void loadQuizzes() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
        
        db.collection("quizzes")
                .whereEqualTo("createdBy", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Lỗi tải quiz: " + error.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    quizList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Quiz quiz = Quiz.fromMap(doc.getData());
                            quiz.setQuizId(doc.getId());
                            quizList.add(quiz);
                        }
                    }

                    updateUI();
                });
    }

    private void updateUI() {
        quizAdapter.setQuizList(quizList);
        
        if (quizList.isEmpty()) {
            rvQuizzes.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvQuizzes.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showCreateQuizDialog(Quiz existingQuiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_quiz, null);
        builder.setView(dialogView);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etQuizTitle = dialogView.findViewById(R.id.etQuizTitle);

        boolean isEditing = existingQuiz != null;
        if (isEditing) {
            tvDialogTitle.setText(R.string.edit_quiz);
            etQuizTitle.setText(existingQuiz.getQuizTitle());
        }

        AlertDialog dialog = builder.create();
        
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String title = etQuizTitle.getText() != null ? etQuizTitle.getText().toString().trim() : "";
            
            if (title.isEmpty()) {
                Toast.makeText(getContext(), R.string.error_empty_title, Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditing) {
                updateQuiz(existingQuiz, title);
            } else {
                createQuiz(title);
            }
            
            dialog.dismiss();
        });

        dialog.show();
    }

    private void createQuiz(String title) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
        Quiz quiz = new Quiz(title, userId);

        db.collection("quizzes")
                .add(quiz.toMap())
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), R.string.quiz_created, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateQuiz(Quiz quiz, String newTitle) {
        quiz.setQuizTitle(newTitle);
        quiz.setUpdatedAt(System.currentTimeMillis());

        db.collection("quizzes")
                .document(quiz.getQuizId())
                .update("quizTitle", newTitle, "updatedAt", quiz.getUpdatedAt())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), R.string.quiz_updated, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onViewQuestions(Quiz quiz) {
        // TODO: Navigate to questions activity or fragment
        Toast.makeText(getContext(), "Xem câu hỏi: " + quiz.getQuizTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddQuestion(Quiz quiz) {
        Toast.makeText(getContext(), "Thêm câu hỏi: " + quiz.getQuizTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditQuiz(Quiz quiz) {
        // Navigate to CreateQuizActivity for editing
        Intent intent = new Intent(getContext(), com.example.edura.CreateQuizActivity.class);
        intent.putExtra("quizId", quiz.getQuizId());
        startActivity(intent);
    }

    @Override
    public void onDeleteQuiz(Quiz quiz) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete_quiz)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    db.collection("quizzes")
                            .document(quiz.getQuizId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), R.string.quiz_deleted, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}


