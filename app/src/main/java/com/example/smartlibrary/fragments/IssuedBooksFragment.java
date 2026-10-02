package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.AddLearningPointActivity;
import com.example.smartlibrary.activities.IssuedBookDetailsActivity;
import com.example.smartlibrary.adapters.IssuedBookAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentIssuedBooksBinding;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.utils.SessionManager;

import java.util.List;

public class IssuedBooksFragment extends Fragment {

    private FragmentIssuedBooksBinding binding;
    private DatabaseReference mDatabase;
    private SessionManager sessionManager;
    private IssuedBookAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIssuedBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sessionManager = new SessionManager(requireContext());

        adapter = new IssuedBookAdapter(new IssuedBookAdapter.OnIssuedBookClickListener() {
            @Override
            public void onIssuedBookClick(IssuedBook issuedBook) {
                Intent intent = new Intent(requireContext(), IssuedBookDetailsActivity.class);
                intent.putExtra(IssuedBookDetailsActivity.EXTRA_ISSUED_ID, issuedBook.getIssuedBookId());
                startActivity(intent);
            }

            @Override
            public void onAddLearningPoint(IssuedBook issuedBook) {
                Intent intent = new Intent(requireContext(), AddLearningPointActivity.class);
                intent.putExtra(AddLearningPointActivity.EXTRA_BOOK_ID, issuedBook.getBookId());
                intent.putExtra(AddLearningPointActivity.EXTRA_BOOK_TITLE, issuedBook.getBookTitle());
                startActivity(intent);
            }

            @Override
            public void onReturnBook(IssuedBook issuedBook) {
                showReturnDialog(issuedBook);
            }
        });

        binding.rvIssuedBooks.setAdapter(adapter);

        loadIssuedBooks();
    }

    private void showReturnDialog(IssuedBook book) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Return Book")
                .setMessage("Return '" + book.getBookTitle() + "' to the library?")
                .setPositiveButton("Return", (dialog, which) -> {
                    mDatabase.child("issuedBooks").child(book.getIssuedBookId()).child("status").setValue("Returned");
                    mDatabase.child("issuedBooks").child(book.getIssuedBookId()).child("returnDate").setValue(System.currentTimeMillis());
                    
                    // Increment available copies
                    mDatabase.child("books").child(book.getBookId()).child("availableCopies").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Integer current = snapshot.getValue(Integer.class);
                                if (current != null) {
                                    mDatabase.child("books").child(book.getBookId()).child("availableCopies").setValue(current + 1);
                                }
                            }
                            Toast.makeText(requireContext(), "Book returned successfully!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadIssuedBooks() {
        String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
        
        mDatabase.child("issuedBooks").orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                List<IssuedBook> list = new java.util.ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    IssuedBook ib = data.getValue(IssuedBook.class);
                    if (ib != null && "Active".equals(ib.getStatus())) {
                        list.add(ib);
                    }
                }
                
                if (list.isEmpty()) {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
                    binding.layoutEmpty.tvEmptyTitle.setText("No Issued Books");
                    binding.layoutEmpty.tvEmptyDescription.setText("You currently have no active borrowed books.");
                    binding.rvIssuedBooks.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
                    binding.rvIssuedBooks.setVisibility(View.VISIBLE);
                    adapter.setIssuedBooks(list);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIssuedBooks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
