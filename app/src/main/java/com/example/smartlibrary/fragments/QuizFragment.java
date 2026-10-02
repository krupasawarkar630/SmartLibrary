package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.R;
import com.example.smartlibrary.activities.QuizResultActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentQuizBinding;
import com.example.smartlibrary.models.QuizQuestion;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;
    private DatabaseReference mDatabase;
    private List<QuizQuestion> questionList = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private int selectedOptionIndex = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("quiz");

        setupOptionButtons();
        
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                questionList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    QuizQuestion q = data.getValue(QuizQuestion.class);
                    if (q != null) questionList.add(q);
                }
                if (questionList.isEmpty()) {
                    Toast.makeText(requireContext(), "No quiz questions available", Toast.LENGTH_SHORT).show();
                } else {
                    displayQuestion();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load quiz", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnNextQuestion.setOnClickListener(v -> handleNextButtonClick());
    }

    private void setupOptionButtons() {
        binding.btnOptA.setOnClickListener(v -> selectOption(0));
        binding.btnOptB.setOnClickListener(v -> selectOption(1));
        binding.btnOptC.setOnClickListener(v -> selectOption(2));
        binding.btnOptD.setOnClickListener(v -> selectOption(3));
    }

    private void selectOption(int optionIndex) {
        selectedOptionIndex = optionIndex;
        resetOptionColors();

        if (optionIndex == 0) highlightSelected(binding.btnOptA);
        else if (optionIndex == 1) highlightSelected(binding.btnOptB);
        else if (optionIndex == 2) highlightSelected(binding.btnOptC);
        else if (optionIndex == 3) highlightSelected(binding.btnOptD);
    }

    private void highlightSelected(com.google.android.material.button.MaterialButton button) {
        button.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.accent));
        button.setStrokeWidth(4);
    }

    private void resetOptionColors() {
        binding.btnOptA.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.card_stroke));
        binding.btnOptB.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.card_stroke));
        binding.btnOptC.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.card_stroke));
        binding.btnOptD.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.card_stroke));

        binding.btnOptA.setStrokeWidth(1);
        binding.btnOptB.setStrokeWidth(1);
        binding.btnOptC.setStrokeWidth(1);
        binding.btnOptD.setStrokeWidth(1);
    }

    private void displayQuestion() {
        if (currentIndex < 0 || currentIndex >= questionList.size()) return;
        selectedOptionIndex = -1;
        resetOptionColors();

        QuizQuestion q = questionList.get(currentIndex);
        binding.tvQuizProgressHeader.setText("Question " + (currentIndex + 1) + " of " + questionList.size());
        binding.tvQuizScoreHeader.setText("Score: " + score);

        int progressPercent = (int) (((float) (currentIndex + 1) / questionList.size()) * 100);
        binding.progressQuiz.setProgress(progressPercent);

        binding.tvQuizQuestion.setText(q.getQuestion());
        binding.btnOptA.setText("A) " + q.getOptionA());
        binding.btnOptB.setText("B) " + q.getOptionB());
        binding.btnOptC.setText("C) " + q.getOptionC());
        binding.btnOptD.setText("D) " + q.getOptionD());

        if (currentIndex == questionList.size() - 1) {
            binding.btnNextQuestion.setText("SUBMIT QUIZ");
        } else {
            binding.btnNextQuestion.setText("NEXT QUESTION");
        }
    }

    private void handleNextButtonClick() {
        if (selectedOptionIndex == -1) {
            Toast.makeText(requireContext(), "Please select an answer to proceed", Toast.LENGTH_SHORT).show();
            return;
        }

        QuizQuestion q = questionList.get(currentIndex);
        if (selectedOptionIndex == q.getCorrectOptionIndex()) {
            score++;
        }

        currentIndex++;
        if (currentIndex < questionList.size()) {
            displayQuestion();
        } else {
            // Finish quiz
            Intent intent = new Intent(requireContext(), QuizResultActivity.class);
            intent.putExtra(QuizResultActivity.EXTRA_SCORE, score);
            intent.putExtra(QuizResultActivity.EXTRA_TOTAL, questionList.size());
            startActivity(intent);

            // Reset state
            currentIndex = 0;
            score = 0;
            displayQuestion();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
