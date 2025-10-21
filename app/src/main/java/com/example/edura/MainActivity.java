package com.example.edura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edura.fragment.HomeFragment;
import com.example.edura.fragment.ProfileFragment;
import com.example.edura.fragment.QuizFragment;
import com.example.edura.fragment.StatsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private LinearLayout navHome, navQuiz, navStats, navProfile;
    private ImageView navHomeIcon, navQuizIcon, navStatsIcon, navProfileIcon;
    private TextView navHomeText, navQuizText, navStatsText, navProfileText;
    
    private int currentTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("MainActivity1", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        // Initialize views
        initViews();
        
        // Setup listeners
        setupListeners();
        
        // Load default fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), 0);
        }
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navQuiz = findViewById(R.id.navQuiz);
        navStats = findViewById(R.id.navStats);
        navProfile = findViewById(R.id.navProfile);
        
        navHomeIcon = findViewById(R.id.navHomeIcon);
        navQuizIcon = findViewById(R.id.navQuizIcon);
        navStatsIcon = findViewById(R.id.navStatsIcon);
        navProfileIcon = findViewById(R.id.navProfileIcon);
        
        navHomeText = findViewById(R.id.navHomeText);
        navQuizText = findViewById(R.id.navQuizText);
        navStatsText = findViewById(R.id.navStatsText);
        navProfileText = findViewById(R.id.navProfileText);
    }

    private void setupListeners() {
        navHome.setOnClickListener(v -> loadFragment(new HomeFragment(), 0));
        navQuiz.setOnClickListener(v -> loadFragment(new QuizFragment(), 1));
        navStats.setOnClickListener(v -> loadFragment(new StatsFragment(), 2));
        navProfile.setOnClickListener(v -> loadFragment(new ProfileFragment(), 3));
    }

    private void loadFragment(Fragment fragment, int tabIndex) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
        
        currentTabIndex = tabIndex;
        updateNavigation(tabIndex);
    }

    public void navigateToFragment(int tabIndex) {
        Fragment fragment;
        switch (tabIndex) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new QuizFragment();
                break;
            case 2:
                fragment = new StatsFragment();
                break;
            case 3:
                fragment = new ProfileFragment();
                break;
            default:
                fragment = new HomeFragment();
        }
        loadFragment(fragment, tabIndex);
    }

    private void updateNavigation(int selectedIndex) {
        // Reset all
        int unselectedColor = Color.parseColor("#9E9E9E");
        int selectedColor = Color.parseColor("#4169E1");
        
        navHomeIcon.setColorFilter(unselectedColor);
        navQuizIcon.setColorFilter(unselectedColor);
        navStatsIcon.setColorFilter(unselectedColor);
        navProfileIcon.setColorFilter(unselectedColor);
        
        navHomeText.setTextColor(unselectedColor);
        navQuizText.setTextColor(unselectedColor);
        navStatsText.setTextColor(unselectedColor);
        navProfileText.setTextColor(unselectedColor);
        
        // Set selected
        switch (selectedIndex) {
            case 0:
                navHomeIcon.setColorFilter(selectedColor);
                navHomeText.setTextColor(selectedColor);
                break;
            case 1:
                navQuizIcon.setColorFilter(selectedColor);
                navQuizText.setTextColor(selectedColor);
                break;
            case 2:
                navStatsIcon.setColorFilter(selectedColor);
                navStatsText.setTextColor(selectedColor);
                break;
            case 3:
                navProfileIcon.setColorFilter(selectedColor);
                navProfileText.setTextColor(selectedColor);
                break;
        }
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity1", "onStart called");
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}
