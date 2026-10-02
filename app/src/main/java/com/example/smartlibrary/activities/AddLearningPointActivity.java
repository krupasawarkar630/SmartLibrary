package com.example.smartlibrary.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityAddLearningPointBinding;
import com.example.smartlibrary.models.LearningPoint;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.UUID;

public class AddLearningPointActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_ID = "extra_book_id";
    public static final String EXTRA_BOOK_TITLE = "extra_book_title";

    private ActivityAddLearningPointBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String bookId;
    private String bookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLearningPointBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        binding.btnBackAddLp.setOnClickListener(v -> finish());

        bookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        bookTitle = getIntent().getStringExtra(EXTRA_BOOK_TITLE);

        if (bookTitle != null) {
            binding.tvAddLpBookTitle.setText("Book: " + bookTitle);
        }

        binding.btnSaveLp.setOnClickListener(v -> saveInsight());
    }

    private void saveInsight() {
        String insight = binding.etInsight.getText() != null ? binding.etInsight.getText().toString().trim() : "";
        float rating = binding.ratingBarAddLp.getRating();

        if (insight.isEmpty()) {
            binding.tilInsight.setError("Please write a takeaway or key insight from the book");
            return;
        } else binding.tilInsight.setError(null);

        User user = sessionManager.getUserSession();
        String userId = user != null ? user.getUserId() : "user_101";

        LearningPoint lp = new LearningPoint(
                UUID.randomUUID().toString(),
                userId,
                bookId != null ? bookId : "BK_001",
                bookTitle != null ? bookTitle : "Smart Book",
                insight,
                rating,
                System.currentTimeMillis()
        );

        FirebaseDatabase.getInstance().getReference("learningPoints").child(lp.getId()).setValue(lp).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Learning point saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save learning point", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
