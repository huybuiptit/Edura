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
        
        // Create a custom LinearLayoutManager that doesn't scroll
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false; // Disable vertical scrolling
            }
            
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                // Force RecyclerView to measure all items
                android.util.Log.d("QuizFragment", "RecyclerView layout completed, item count: " + getItemCount());
            }
        };
        
        rvQuizzes.setLayoutManager(layoutManager);
        rvQuizzes.setHasFixedSize(false);
        rvQuizzes.setNestedScrollingEnabled(false);
//        rvQuizzes.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvQuizzes.setAdapter(quizAdapter);
        
        // Force RecyclerView to measure all items
        rvQuizzes.post(() -> {
            android.util.Log.d("QuizFragment", "RecyclerView post: item count = " + quizAdapter.getItemCount());
            rvQuizzes.requestLayout();
        });
    }

    private void setupListeners() {
        cardAiQuiz.setOnClickListener(v -> {
            // Navigate to CreateAIQuizActivity
            Intent intent = new Intent(getContext(), com.example.edura.CreateAIQuizActivity.class);
            startActivity(intent);
        });
        
        cardManualQuiz.setOnClickListener(v -> {
            // Navigate to CreateQuizActivity
            Intent intent = new Intent(getContext(), com.example.edura.CreateQuizActivity.class);
            startActivity(intent);
        });
    }

    private void loadQuizzes() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
        
        android.util.Log.d("QuizFragment", "Starting to load quizzes for user: " + userId);

        // First try: Load quizzes with proper createdBy field
        db.collection("quizzes")
                .whereEqualTo("createdBy", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    android.util.Log.d("QuizFragment", "Found " + querySnapshot.size() + " quizzes with createdBy=" + userId);
                    
                    quizList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            Quiz quiz = Quiz.fromMap(doc.getData());
                            quiz.setQuizId(doc.getId());
                            quizList.add(quiz);
                            android.util.Log.d("QuizFragment", "Added quiz: " + quiz.getQuizTitle());
                        } catch (Exception e) {
                            android.util.Log.e("QuizFragment", "Error parsing quiz " + doc.getId() + ": " + e.getMessage());
                        }
                    }
                    
                    // Second try: Load quizzes with null createdBy (legacy data)
                    loadLegacyQuizzes(userId);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("QuizFragment", "Error loading quizzes: " + e.getMessage());
                    Toast.makeText(getContext(), "Lỗi tải quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void loadLegacyQuizzes(String userId) {
        // Load quizzes with null createdBy (legacy data)
        db.collection("quizzes")
                .whereEqualTo("createdBy", null)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    android.util.Log.d("QuizFragment", "Found " + querySnapshot.size() + " legacy quizzes with createdBy=null");
                    
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            Quiz quiz = Quiz.fromMap(doc.getData());
                            quiz.setQuizId(doc.getId());
                            quizList.add(quiz);
                            android.util.Log.d("QuizFragment", "Added legacy quiz: " + quiz.getQuizTitle());
                        } catch (Exception e) {
                            android.util.Log.e("QuizFragment", "Error parsing legacy quiz " + doc.getId() + ": " + e.getMessage());
                        }
                    }
                    
                    // Third try: Load ALL quizzes and filter manually (fallback)
                    loadAllQuizzesFallback(userId);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("QuizFragment", "Error loading legacy quizzes: " + e.getMessage());
                    // Continue to fallback method
                    loadAllQuizzesFallback(userId);
                });
    }
    
    private void loadAllQuizzesFallback(String userId) {
        // Fallback: Load ALL quizzes and filter manually
        db.collection("quizzes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    android.util.Log.d("QuizFragment", "Fallback: Found " + querySnapshot.size() + " total quizzes in database");
                    
                    int addedCount = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            Quiz quiz = Quiz.fromMap(doc.getData());
                            quiz.setQuizId(doc.getId());
                            
                            // Check if this quiz is already in our list
                            boolean alreadyExists = false;
                            for (Quiz existingQuiz : quizList) {
                                if (existingQuiz.getQuizId().equals(quiz.getQuizId())) {
                                    alreadyExists = true;
                                    break;
                                }
                            }
                            
                            if (!alreadyExists) {
                                String createdBy = quiz.getCreatedBy();
                                // Include if no createdBy field, empty, or matches user
                                if (createdBy == null || createdBy.isEmpty() || userId.equals(createdBy)) {
                                    quizList.add(quiz);
                                    addedCount++;
                                    android.util.Log.d("QuizFragment", "Added fallback quiz: " + quiz.getQuizTitle() + " (createdBy: " + createdBy + ")");
                                }
                            }
                        } catch (Exception e) {
                            android.util.Log.e("QuizFragment", "Error parsing fallback quiz " + doc.getId() + ": " + e.getMessage());
                        }
                    }
                    
                    // Sort by createdAt descending
                    quizList.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
                    
                    android.util.Log.d("QuizFragment", "Final result: " + quizList.size() + " quizzes loaded (added " + addedCount + " from fallback)");
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("QuizFragment", "Error in fallback loading: " + e.getMessage());
                    Toast.makeText(getContext(), "Lỗi tải quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    updateUI();
                });
    }

    private void updateUI() {
        quizAdapter.setQuizList(quizList);
        
        // Debug: Show detailed info about loaded quizzes
        android.util.Log.d("QuizFragment", "=== QUIZ LOADING SUMMARY ===");
        android.util.Log.d("QuizFragment", "Total quizzes loaded: " + quizList.size());
        for (int i = 0; i < quizList.size(); i++) {
            Quiz quiz = quizList.get(i);
            android.util.Log.d("QuizFragment", "Quiz " + (i+1) + ": " + quiz.getQuizTitle() + 
                    " (ID: " + quiz.getQuizId() + ", createdBy: " + quiz.getCreatedBy() + 
                    ", createdAt: " + quiz.getCreatedAt() + ")");
        }
        android.util.Log.d("QuizFragment", "=============================");
        
        if (quizList.isEmpty()) {
            rvQuizzes.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            android.util.Log.d("QuizFragment", "Showing empty state - no quizzes found");
        } else {
            rvQuizzes.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            android.util.Log.d("QuizFragment", "Showing " + quizList.size() + " quizzes in RecyclerView");
            
            // Force RecyclerView to refresh and show all items
            rvQuizzes.post(() -> {
                android.util.Log.d("QuizFragment", "RecyclerView refresh: adapter item count = " + quizAdapter.getItemCount());
                quizAdapter.notifyDataSetChanged();
                rvQuizzes.requestLayout();
                
                // Additional debug: check RecyclerView child count
                rvQuizzes.postDelayed(() -> {
                    int childCount = rvQuizzes.getChildCount();
                    android.util.Log.d("QuizFragment", "RecyclerView child count: " + childCount + " (should be " + quizList.size() + ")");
                }, 100);
            });
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
        Intent intent = new Intent(getContext(), com.example.edura.QuizDetailActivity.class);
        intent.putExtra("quizId", quiz.getQuizId());
        startActivity(intent);
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
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload quizzes when fragment becomes visible
        android.util.Log.d("QuizFragment", "Fragment resumed, reloading quizzes...");
        loadQuizzes();
    }
}


