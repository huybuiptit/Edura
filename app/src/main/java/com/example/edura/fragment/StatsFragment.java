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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.R;
import com.example.edura.adapter.TestHistoryAdapter;
import com.example.edura.model.TestResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private RecyclerView rvTestHistory;
    private View emptyState;
    private TextView tvTotalTests;
    private TestHistoryAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<TestResult> testResults = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initViews(view);
        setupRecyclerView();
        loadTestHistory();

        return view;
    }

    private void initViews(View view) {
        rvTestHistory = view.findViewById(R.id.rvTestHistory);
        emptyState = view.findViewById(R.id.emptyState);
        tvTotalTests = view.findViewById(R.id.tvTotalTests);
    }

    private void setupRecyclerView() {
        adapter = new TestHistoryAdapter();
        rvTestHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTestHistory.setAdapter(adapter);
    }

    private void loadTestHistory() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        db.collection("test_results")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Lỗi tải lịch sử: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    testResults.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            TestResult result = TestResult.fromMap(doc.getData());
                            result.setResultId(doc.getId());
                            testResults.add(result);
                        }
                    }

                    updateUI();
                });
    }

    private void updateUI() {
        adapter.setTestResults(testResults);

        tvTotalTests.setText(testResults.size() + " bài kiểm tra");

        if (testResults.isEmpty()) {
            rvTestHistory.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvTestHistory.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload data when fragment is visible again
        if (db != null && auth != null) {
            loadTestHistory();
        }
    }
}


