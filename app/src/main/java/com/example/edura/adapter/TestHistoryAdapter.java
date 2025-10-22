package com.example.edura.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.R;
import com.example.edura.model.TestResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestHistoryAdapter extends RecyclerView.Adapter<TestHistoryAdapter.TestHistoryViewHolder> {

    private List<TestResult> testResults = new ArrayList<>();

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TestHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_history, parent, false);
        return new TestHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestHistoryViewHolder holder, int position) {
        TestResult result = testResults.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return testResults.size();
    }

    static class TestHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizName, tvScore, tvDate, tvScoreBadge;

        public TestHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizName = itemView.findViewById(R.id.tvQuizName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvScoreBadge = itemView.findViewById(R.id.tvScoreBadge);
        }

        public void bind(TestResult result) {
            tvQuizName.setText(result.getQuizTitle());
            tvScore.setText(result.getCorrectAnswers() + "/" + result.getTotalQuestions() + " câu đúng");
            
            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
            String dateStr = sdf.format(new Date(result.getTimestamp()));
            tvDate.setText(dateStr);
            
            // Score percentage
            tvScoreBadge.setText(result.getScorePercentage() + "%");
        }
    }
}

