package com.example.edura.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.edura.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private MaterialCardView playAndWinCard;
    private MaterialCardView biologyItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initViews(view);
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        playAndWinCard = view.findViewById(R.id.playAndWinCard);
        biologyItem = view.findViewById(R.id.biologyItem);
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


