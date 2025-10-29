package com.example.edura.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.R;
import com.example.edura.model.Quiz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList = new ArrayList<>();
    private OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onViewQuestions(Quiz quiz);
        void onAddQuestion(Quiz quiz);
        void onEditQuiz(Quiz quiz);
        void onDeleteQuiz(Quiz quiz);
    }

    public QuizAdapter(OnQuizClickListener listener) {
        this.listener = listener;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
        android.util.Log.d("QuizAdapter", "setQuizList called with " + quizList.size() + " quizzes");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_simple, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        android.util.Log.d("QuizAdapter", "onBindViewHolder called for position " + position);
        Quiz quiz = quizList.get(position);
        holder.bind(quiz);
    }

    @Override
    public int getItemCount() {
        android.util.Log.d("QuizAdapter", "getItemCount() called, returning " + quizList.size());
        return quizList.size();
    }

    class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizTitle, tvQuestionCount, tvCreatedDate;
        ImageButton btnMenu;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tvQuizTitle);
            tvQuestionCount = itemView.findViewById(R.id.tvQuestionCount);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }

        public void bind(Quiz quiz) {
            tvQuizTitle.setText(quiz.getQuizTitle());
            
            // Question count
            int questionCount = quiz.getQuestionCount();
            tvQuestionCount.setText(questionCount + " câu hỏi");
            
            // Created date with time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
            String dateStr = sdf.format(new Date(quiz.getCreatedAt()));
            tvCreatedDate.setText(dateStr);

            // Click card to view questions
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewQuestions(quiz);
                }
            });

            // Menu button (3 dots)
            btnMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.quiz_menu, popupMenu.getMenu());
                
                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_edit) {
                        if (listener != null) {
                            listener.onEditQuiz(quiz);
                        }
                        return true;
                    } else if (itemId == R.id.action_delete) {
                        if (listener != null) {
                            listener.onDeleteQuiz(quiz);
                        }
                        return true;
                    }
                    return false;
                });
                
                popupMenu.show();
            });
        }
    }
}
