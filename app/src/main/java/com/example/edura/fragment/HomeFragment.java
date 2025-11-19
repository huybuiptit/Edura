package com.example.edura.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.R;
import com.example.edura.adapter.CategoryAdapter;
import com.example.edura.model.QuizCategory;
import com.example.edura.model.RecentQuiz;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvUsername;
    private MaterialCardView btnNotification;
    private EditText etSearch;
    private ImageView btnClearSearch;
    private RecyclerView recyclerCategories;
    private TextView tvSeeMoreWorld;
    private LinearLayout recentQuizzesContainer;
    private FirebaseAuth auth;
    
    private CategoryAdapter categoryAdapter;
    private List<QuizCategory> categories;
    private List<RecentQuiz> recentQuizzes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            
            auth = FirebaseAuth.getInstance();
            
            initViews(view);
            updateUsername();
            setupCategories();
            setupRecentQuizzes();
            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error loading home screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        
        return view;
    }

    private void initViews(View view) {
        if (view == null) return;
        
        tvUsername = view.findViewById(R.id.tvUsername);
        btnNotification = view.findViewById(R.id.btnNotification);
        etSearch = view.findViewById(R.id.etSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        tvSeeMoreWorld = view.findViewById(R.id.tvSeeMoreWorld);
        recentQuizzesContainer = view.findViewById(R.id.recentQuizzesContainer);
        
        // Log if any view is null to help debug
        if (tvUsername == null || btnNotification == null || etSearch == null 
            || btnClearSearch == null || recyclerCategories == null 
            || tvSeeMoreWorld == null || recentQuizzesContainer == null) {
            android.util.Log.e("HomeFragment", "One or more views are null!");
        }
    }
    
    private void updateUsername() {
        if (tvUsername == null || auth == null) return;
        
        try {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String displayName = currentUser.getDisplayName();
                String email = currentUser.getEmail();
                
                String userName;
                if (displayName != null && !displayName.isEmpty()) {
                    userName = displayName;
                } else if (email != null) {
                    userName = email.split("@")[0];
                } else {
                    userName = "User";
                }
                
                tvUsername.setText(userName);
            } else {
                tvUsername.setText("User");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvUsername.setText("User");
        }
    }

    private void setupCategories() {
        if (getContext() == null || recyclerCategories == null) return;
        
        try {
            categories = new ArrayList<>();
            categories.add(new QuizCategory("History", R.drawable.bg_category_gradient_1));
            categories.add(new QuizCategory("Programming", R.drawable.bg_category_gradient_2));
            categories.add(new QuizCategory("Architecture", R.drawable.bg_category_gradient_3));
            categories.add(new QuizCategory("Art", R.drawable.bg_category_gradient_4));
            categories.add(new QuizCategory("Soccer", R.drawable.bg_category_gradient_5));
            categories.add(new QuizCategory("Math", R.drawable.bg_category_gradient_6));
            categories.add(new QuizCategory("Biology", R.drawable.bg_category_gradient_7));
            categories.add(new QuizCategory("Chemistry", R.drawable.bg_category_gradient_8));
            
            categoryAdapter = new CategoryAdapter(categories, category -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Selected: " + category.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            
            // Setup horizontal LinearLayoutManager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerCategories.setLayoutManager(layoutManager);
            recyclerCategories.setAdapter(categoryAdapter);
            recyclerCategories.setHasFixedSize(true);
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("HomeFragment", "Error setting up categories: " + e.getMessage());
        }
    }

    private void setupRecentQuizzes() {
        if (getContext() == null || recentQuizzesContainer == null) return;
        
        recentQuizzes = new ArrayList<>();
        recentQuizzes.add(new RecentQuiz("CB – Lesson 10 – READING", "14/54 thẻ", "IELTSHHieuMinh", R.drawable.ic_quiz));
        recentQuizzes.add(new RecentQuiz("Math Practice Quiz", "8/20 thẻ", "MathTeacher", R.drawable.ic_math));
        recentQuizzes.add(new RecentQuiz("Biology Basics", "15/30 thẻ", "BiologyPro", R.drawable.ic_biology));
        
        // Add recent quiz items to container
        for (RecentQuiz quiz : recentQuizzes) {
            try {
                View quizItemView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_recent_quiz, recentQuizzesContainer, false);
                
                ImageView ivQuizIcon = quizItemView.findViewById(R.id.ivQuizIcon);
                TextView tvQuizTitle = quizItemView.findViewById(R.id.tvQuizTitle);
                TextView tvQuizProgress = quizItemView.findViewById(R.id.tvQuizProgress);
                TextView tvQuizAuthor = quizItemView.findViewById(R.id.tvQuizAuthor);
                
                ivQuizIcon.setImageResource(quiz.getIconResId());
                tvQuizTitle.setText(quiz.getTitle());
                tvQuizProgress.setText(quiz.getProgress());
                tvQuizAuthor.setText("Tác giả: " + quiz.getAuthor());
                
                quizItemView.setOnClickListener(v -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Opened: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                
                recentQuizzesContainer.addView(quizItemView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupListeners() {
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (tvSeeMoreWorld != null) {
            tvSeeMoreWorld.setOnClickListener(v -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "See more World Quiz", Toast.LENGTH_SHORT).show();
                }
                // Navigate to categories page or expand list
            });
        }
        
        // Setup search bar
        setupSearchBar();
    }
    
    private void setupSearchBar() {
        if (etSearch == null || btnClearSearch == null) return;
        
        // Text change listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                if (s.length() > 0) {
                    btnClearSearch.setVisibility(View.VISIBLE);
                } else {
                    btnClearSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Perform search
                performSearch(s.toString());
            }
        });
        
        // Clear button listener
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.clearFocus();
            // Hide keyboard
            hideKeyboard();
        });
        
        // Search action on keyboard
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString());
                hideKeyboard();
                return true;
            }
            return false;
        });
    }
    
    private void performSearch(String query) {
        if (getContext() == null) return;
        
        if (query.isEmpty()) {
            // Show all items
            return;
        }
        
        // Perform search - can be implemented later with real data
        Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
        
        // TODO: Filter categories and recent quizzes based on query
        // filterCategories(query);
        // filterRecentQuizzes(query);
    }
    
    private void hideKeyboard() {
        if (getActivity() != null && etSearch != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            }
        }
    }
}


