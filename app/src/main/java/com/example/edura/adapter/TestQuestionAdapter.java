package com.example.edura.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edura.R;
import com.example.edura.model.Answer;
import com.example.edura.model.Question;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestQuestionAdapter extends RecyclerView.Adapter<TestQuestionAdapter.QuestionViewHolder> {

    private List<Question> questions;
    private boolean showCorrectAnswer;
    
    // For single choice: questionIndex -> answerIndex
    private Map<Integer, Integer> singleChoiceAnswers = new HashMap<>();
    
    // For multiple choice: questionIndex -> Set of answer indices
    private Map<Integer, Set<Integer>> multipleChoiceAnswers = new HashMap<>();
    
    // For fill in blank: questionIndex -> answer text
    private Map<Integer, String> fillInBlankAnswers = new HashMap<>();

    public TestQuestionAdapter(List<Question> questions, boolean showCorrectAnswer) {
        this.questions = questions;
        this.showCorrectAnswer = showCorrectAnswer;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public double getTotalScore() {
        double totalScore = 0.0;
        
        for (int i = 0; i < questions.size(); i++) {
            totalScore += getQuestionScore(i);
        }
        
        return totalScore;
    }
    
    private double getQuestionScore(int questionIndex) {
        Question question = questions.get(questionIndex);
        Question.QuestionType type = question.getQuestionType();
        
        if (type == Question.QuestionType.SINGLE_CHOICE) {
            return getSingleChoiceScore(questionIndex);
        } else if (type == Question.QuestionType.MULTIPLE_CHOICE) {
            return getMultipleChoiceScore(questionIndex);
        } else if (type == Question.QuestionType.FILL_IN_BLANK) {
            return getFillInBlankScore(questionIndex);
        }
        
        return 0.0;
    }
    
    private double getSingleChoiceScore(int questionIndex) {
        if (!singleChoiceAnswers.containsKey(questionIndex)) {
            return 0.0;
        }
        
        Question question = questions.get(questionIndex);
        int selectedIndex = singleChoiceAnswers.get(questionIndex);
        
        if (selectedIndex >= 0 && selectedIndex < question.getAnswers().size()) {
            return question.getAnswers().get(selectedIndex).getCorrect() ? 1.0 : 0.0;
        }
        
        return 0.0;
    }
    
    private double getMultipleChoiceScore(int questionIndex) {
        Question question = questions.get(questionIndex);
        Set<Integer> selectedIndices = multipleChoiceAnswers.get(questionIndex);
        
        if (selectedIndices == null || selectedIndices.isEmpty()) {
            return 0.0;
        }
        
        // Count correct answers in the question
        List<Integer> correctIndices = new ArrayList<>();
        for (int i = 0; i < question.getAnswers().size(); i++) {
            if (question.getAnswers().get(i).getCorrect()) {
                correctIndices.add(i);
            }
        }
        
        if (correctIndices.isEmpty()) {
            return 0.0;
        }
        
        // Count how many correct answers were selected
        int correctSelected = 0;
        for (int index : selectedIndices) {
            if (correctIndices.contains(index)) {
                correctSelected++;
            }
        }
        
        // Partial credit: (số câu đúng chọn được) / (tổng số câu đúng)
        return (double) correctSelected / correctIndices.size();
    }
    
    private double getFillInBlankScore(int questionIndex) {
        String userAnswer = fillInBlankAnswers.get(questionIndex);
        
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return 0.0;
        }
        
        Question question = questions.get(questionIndex);
        if (question.getAnswers().isEmpty()) {
            return 0.0;
        }
        
        String correctAnswer = question.getAnswers().get(0).getAnswerText().trim().toLowerCase();
        userAnswer = userAnswer.trim().toLowerCase();
        
        return correctAnswer.equals(userAnswer) ? 1.0 : 0.0;
    }

    public int getCorrectAnswersCount() {
        int correct = 0;
        android.util.Log.d("TestAdapter", "Calculating correct answers for " + questions.size() + " questions");
        
        for (int i = 0; i < questions.size(); i++) {
            double score = getQuestionScore(i);
            android.util.Log.d("TestAdapter", "Q" + i + " score: " + score);
            
            // Chỉ đếm là đúng nếu đạt 100% điểm
            if (score >= 1.0) {
                correct++;
            }
        }
        
        android.util.Log.d("TestAdapter", "Total fully correct: " + correct);
        return correct;
    }

    public int getAnsweredQuestionsCount() {
        int answered = 0;
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Question.QuestionType type = question.getQuestionType();
            
            if (type == Question.QuestionType.SINGLE_CHOICE && singleChoiceAnswers.containsKey(i)) {
                answered++;
            } else if (type == Question.QuestionType.MULTIPLE_CHOICE && multipleChoiceAnswers.containsKey(i) 
                    && !multipleChoiceAnswers.get(i).isEmpty()) {
                answered++;
            } else if (type == Question.QuestionType.FILL_IN_BLANK && fillInBlankAnswers.containsKey(i) 
                    && !fillInBlankAnswers.get(i).trim().isEmpty()) {
                answered++;
            }
        }
        
        return answered;
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestionText;
        RadioGroup radioGroupAnswers;
        LinearLayout checkboxGroupAnswers;
        TextInputLayout fillInBlankLayout;
        TextInputEditText etFillInBlank;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            radioGroupAnswers = itemView.findViewById(R.id.radioGroupAnswers);
            checkboxGroupAnswers = itemView.findViewById(R.id.checkboxGroupAnswers);
            fillInBlankLayout = itemView.findViewById(R.id.fillInBlankLayout);
            etFillInBlank = itemView.findViewById(R.id.etFillInBlank);
        }

        public void bind(Question question, int questionIndex) {
            int questionNumber = questionIndex + 1;
            tvQuestionNumber.setText("Question " + questionNumber + " of " + questions.size());
            tvQuestionText.setText(question.getQuestionText());

            // Hide all answer types first
            radioGroupAnswers.setVisibility(View.GONE);
            checkboxGroupAnswers.setVisibility(View.GONE);
            fillInBlankLayout.setVisibility(View.GONE);

            Question.QuestionType type = question.getQuestionType();
            
            if (type == Question.QuestionType.SINGLE_CHOICE) {
                bindSingleChoice(question, questionIndex);
            } else if (type == Question.QuestionType.MULTIPLE_CHOICE) {
                bindMultipleChoice(question, questionIndex);
            } else if (type == Question.QuestionType.FILL_IN_BLANK) {
                bindFillInBlank(question, questionIndex);
            }
        }
        
        private void bindSingleChoice(Question question, int questionIndex) {
            radioGroupAnswers.setVisibility(View.VISIBLE);
            radioGroupAnswers.removeAllViews();
            radioGroupAnswers.clearCheck();

            List<Answer> answers = question.getAnswers();
            for (int i = 0; i < answers.size(); i++) {
                Answer answer = answers.get(i);
                RadioButton radioButton = new RadioButton(itemView.getContext());
                radioButton.setId(View.generateViewId());
                radioButton.setText(answer.getAnswerText());
                radioButton.setTextSize(16);
                radioButton.setPadding(32, 24, 32, 24);
                
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 24);
                radioButton.setLayoutParams(params);

                radioGroupAnswers.addView(radioButton);

                final int answerIndex = i;
                
                // Check if this answer was previously selected
                boolean isSelected = singleChoiceAnswers.containsKey(questionIndex) 
                        && singleChoiceAnswers.get(questionIndex) == answerIndex;
                radioButton.setChecked(isSelected);
                radioButton.setBackgroundResource(isSelected ? R.drawable.bg_answer_selected : R.drawable.bg_answer_option);
            }
            
            // Add listener to RadioGroup
            radioGroupAnswers.setOnCheckedChangeListener((group, checkedId) -> {
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    if (rb.getId() == checkedId) {
                        singleChoiceAnswers.put(questionIndex, i);
                        rb.setBackgroundResource(R.drawable.bg_answer_selected);
                        android.util.Log.d("TestAdapter", "Q" + questionIndex + " (Single) selected: " + i);
                    } else {
                        rb.setBackgroundResource(R.drawable.bg_answer_option);
                    }
                }
            });
        }
        
        private void bindMultipleChoice(Question question, int questionIndex) {
            checkboxGroupAnswers.setVisibility(View.VISIBLE);
            checkboxGroupAnswers.removeAllViews();

            List<Answer> answers = question.getAnswers();
            Set<Integer> selectedSet = multipleChoiceAnswers.get(questionIndex);
            if (selectedSet == null) {
                selectedSet = new HashSet<>();
                multipleChoiceAnswers.put(questionIndex, selectedSet);
            }
            
            final Set<Integer> currentSelection = selectedSet;

            for (int i = 0; i < answers.size(); i++) {
                Answer answer = answers.get(i);
                CheckBox checkBox = new CheckBox(itemView.getContext());
                checkBox.setId(View.generateViewId());
                checkBox.setText(answer.getAnswerText());
                checkBox.setTextSize(16);
                checkBox.setPadding(32, 24, 32, 24);
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 24);
                checkBox.setLayoutParams(params);

                final int answerIndex = i;
                
                // Check if this answer was previously selected
                boolean isSelected = currentSelection.contains(answerIndex);
                checkBox.setChecked(isSelected);
                checkBox.setBackgroundResource(isSelected ? R.drawable.bg_answer_selected : R.drawable.bg_answer_option);
                
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        currentSelection.add(answerIndex);
                        checkBox.setBackgroundResource(R.drawable.bg_answer_selected);
                    } else {
                        currentSelection.remove(answerIndex);
                        checkBox.setBackgroundResource(R.drawable.bg_answer_option);
                    }
                    android.util.Log.d("TestAdapter", "Q" + questionIndex + " (Multiple) selection: " + currentSelection);
                });

                checkboxGroupAnswers.addView(checkBox);
            }
        }
        
        private void bindFillInBlank(Question question, int questionIndex) {
            fillInBlankLayout.setVisibility(View.VISIBLE);
            
            // Restore previous answer
            String previousAnswer = fillInBlankAnswers.get(questionIndex);
            if (previousAnswer != null) {
                etFillInBlank.setText(previousAnswer);
            } else {
                etFillInBlank.setText("");
            }
            
            // Remove previous listeners
            etFillInBlank.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    fillInBlankAnswers.put(questionIndex, s.toString());
                    android.util.Log.d("TestAdapter", "Q" + questionIndex + " (FillBlank) answer: " + s.toString());
                }
            });
        }
    }
}
