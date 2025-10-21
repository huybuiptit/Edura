package com.example.edura.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.edura.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private MaterialCardView playAndWinCard;
    private MaterialCardView biologyItem;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        auth = FirebaseAuth.getInstance();
        
        initViews(view);
        setupListeners();
        updateWelcomeMessage();
        
        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        playAndWinCard = view.findViewById(R.id.playAndWinCard);
        biologyItem = view.findViewById(R.id.biologyItem);
    }
    
    private void updateWelcomeMessage() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            
            // Sử dụng displayName nếu có, nếu không thì lấy phần trước @ của email
            String userName;
            if (displayName != null && !displayName.isEmpty()) {
                userName = displayName;
            } else if (email != null) {
                userName = email.split("@")[0];
            } else {
                userName = "User";
            }
            
            tvWelcome.setText("Welcome, " + userName);
        } else {
            tvWelcome.setText("Welcome");
        }
    }

    private void setupListeners() {
        playAndWinCard.setOnClickListener(v -> {
            // Navigate to Quiz fragment
            navigateToQuiz();
        });

        biologyItem.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Quiz selected", Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateToQuiz() {
        if (getActivity() != null && getActivity() instanceof com.example.edura.MainActivity) {
            ((com.example.edura.MainActivity) getActivity()).navigateToFragment(1); // Quiz tab
        }
    }
}


