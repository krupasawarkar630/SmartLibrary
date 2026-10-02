package com.example.smartlibrary.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityQuizResultBinding;
import com.example.smartlibrary.models.QuizResult;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.Locale;
import java.util.UUID;

public class QuizResultActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extra_score";
    public static final String EXTRA_TOTAL = "extra_total";

    private ActivityQuizResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackQuizResult.setOnClickListener(v -> finish());

        int score = getIntent().getIntExtra(EXTRA_SCORE, 0);
        int total = getIntent().getIntExtra(EXTRA_TOTAL, 4);

        float percentage = total > 0 ? ((float) score / total) * 100f : 0f;

        binding.tvQuizResultScore.setText("Score: " + score + " / " + total);
        binding.tvQuizResultPercentage.setText(String.format(Locale.getDefault(), "%.1f%% Grade", percentage));

        if (percentage >= 75f) {
            binding.tvQuizResultFeedback.setText("Outstanding Knowledge! You possess excellent library literacy.");
        } else if (percentage >= 50f) {
            binding.tvQuizResultFeedback.setText("Good Effort! You have a solid grasp of library systems.");
        } else {
            binding.tvQuizResultFeedback.setText("Keep Learning! Review library features to boost your score next time.");
        }

        // Save result
        SessionManager sessionManager = new SessionManager(this);
        User user = sessionManager.getUserSession();
        if (user != null) {
            QuizResult qr = new QuizResult(
                    UUID.randomUUID().toString(),
                    user.getUserId(),
                    user.getName(),
                    score, total, percentage, System.currentTimeMillis()
            );
            FirebaseDatabase.getInstance().getReference("quizResults").child(qr.getId()).setValue(qr);
        }

        binding.btnRetryQuiz.setOnClickListener(v -> finish());
    }
}
