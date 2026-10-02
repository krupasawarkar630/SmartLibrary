package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityIssuedBookDetailsBinding;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.utils.DateUtils;
import com.example.smartlibrary.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class IssuedBookDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ISSUED_ID = "extra_issued_id";

    private ActivityIssuedBookDetailsBinding binding;
    private DatabaseReference mDatabase;
    private IssuedBook currentIssuedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIssuedBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.btnBackIssuedDetails.setOnClickListener(v -> finish());

        String issuedId = getIntent().getStringExtra(EXTRA_ISSUED_ID);
        if (issuedId != null) {
            mDatabase.child("issuedBooks").child(issuedId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentIssuedBook = snapshot.getValue(IssuedBook.class);
                        if (currentIssuedBook != null) {
                            displayIssuedDetails();
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    finish();
                }
            });
        } else {
            finish();
        }

        binding.btnSubmitLearning.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLearningPointActivity.class);
            intent.putExtra(AddLearningPointActivity.EXTRA_BOOK_ID, currentIssuedBook.getBookId());
            intent.putExtra(AddLearningPointActivity.EXTRA_BOOK_TITLE, currentIssuedBook.getBookTitle());
            startActivity(intent);
        });

        binding.btnConfirmReturn.setOnClickListener(v -> showReturnConfirmationDialog());
    }

    private void displayIssuedDetails() {
        binding.tvIssuedBookDetailsTitle.setText(currentIssuedBook.getBookTitle());
        binding.tvIssuedBookDetailsAuthor.setText("by " + currentIssuedBook.getBookAuthor());
        binding.tvIssuedBookIssueDate.setText("Issue Date: " + DateUtils.formatDate(currentIssuedBook.getIssueDate()));
        binding.tvIssuedBookDueDate.setText("Due Date: " + DateUtils.formatDate(currentIssuedBook.getDueDate()));

        double fine = DateUtils.calculateFine(currentIssuedBook.getDueDate());
        if (fine > 0) {
            binding.tvIssuedBookFine.setText("Overdue Fine: $" + String.format(java.util.Locale.getDefault(), "%.2f", fine));
            binding.tvIssuedBookFine.setTextColor(getColor(com.example.smartlibrary.R.color.danger));
        } else {
            binding.tvIssuedBookFine.setText("Overdue Fine: $0.00 (No Fines)");
            binding.tvIssuedBookFine.setTextColor(getColor(com.example.smartlibrary.R.color.success));
        }

        if (currentIssuedBook.getLearningPoint() != null && !currentIssuedBook.getLearningPoint().isEmpty()) {
            binding.tvIssuedBookLearning.setText("Key Insight: " + currentIssuedBook.getLearningPoint());
        } else {
            binding.tvIssuedBookLearning.setText("No learning point notes recorded yet.");
        }
    }

    private void showReturnConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Return Book")
                .setMessage("Are you sure you want to return '" + currentIssuedBook.getBookTitle() + "' to the library?")
                .setPositiveButton("Return Book", (dialog, which) -> {
                    mDatabase.child("issuedBooks").child(currentIssuedBook.getIssuedBookId()).child("status").setValue("Returned");
                    mDatabase.child("issuedBooks").child(currentIssuedBook.getIssuedBookId()).child("returnDate").setValue(System.currentTimeMillis());
                    
                    mDatabase.child("books").child(currentIssuedBook.getBookId()).child("availableCopies").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Integer current = snapshot.getValue(Integer.class);
                                if (current != null) {
                                    mDatabase.child("books").child(currentIssuedBook.getBookId()).child("availableCopies").setValue(current + 1);
                                }
                            }
                            Toast.makeText(IssuedBookDetailsActivity.this, "Book returned successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            finish();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
