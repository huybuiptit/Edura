package com.example.edura.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.R;
import com.example.edura.model.Answer;
import com.example.edura.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList = new ArrayList<>();
    private OnQuestionClickListener listener;

    public interface OnQuestionClickListener {
        void onDeleteQuestion(Question question, int position);
    }

    public QuestionAdapter(OnQuestionClickListener listener) {
        this.listener = listener;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestionText, tvQuestionType;
        LinearLayout llAnswers;
        ImageButton btnDeleteQuestion;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvQuestionType = itemView.findViewById(R.id.tvQuestionType);
            llAnswers = itemView.findViewById(R.id.llAnswers);
            btnDeleteQuestion = itemView.findViewById(R.id.btnDeleteQuestion);
        }

        public void bind(Question question, int position) {
            tvQuestionNumber.setText("Question " + (position + 1));
            tvQuestionText.setText(question.getQuestionText());
            
            // Set question type badge
            if (question.getQuestionType() == Question.QuestionType.SINGLE_CHOICE) {
                tvQuestionType.setText(itemView.getContext().getString(R.string.single_choice));
                tvQuestionType.setBackgroundResource(R.drawable.bg_complete_badge);
            } else {
                tvQuestionType.setText(itemView.getContext().getString(R.string.multiple_choice));
                tvQuestionType.setBackgroundResource(R.drawable.bg_incomplete_badge);
            }

            // Display answers
            llAnswers.removeAllViews();
            List<Answer> answers = question.getAnswers();
            for (int i = 0; i < answers.size(); i++) {
                Answer answer = answers.get(i);
                View answerView = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_answer, llAnswers, false);
                
                TextView tvAnswerText = answerView.findViewById(R.id.tvAnswerText);
                TextView tvCorrectBadge = answerView.findViewById(R.id.tvCorrectBadge);
                
                tvAnswerText.setText(answer.getAnswerText());
                
                if (answer.getCorrect()) {
                    tvCorrectBadge.setVisibility(View.VISIBLE);
                } else {
                    tvCorrectBadge.setVisibility(View.GONE);
                }
                
                llAnswers.addView(answerView);
            }

            // Delete button
            btnDeleteQuestion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteQuestion(question, getAdapterPosition());
                }
            });
        }
    }
}


