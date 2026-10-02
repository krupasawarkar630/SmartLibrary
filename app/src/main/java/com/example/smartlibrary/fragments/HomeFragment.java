package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.BookDetailActivity;
import com.example.smartlibrary.activities.MainActivity;
import com.example.smartlibrary.adapters.BookAdapter;
import com.example.smartlibrary.adapters.CategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentHomeBinding;
import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.Arrays;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.smartlibrary.R;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference mDatabase;
    private SessionManager sessionManager;
    private BookAdapter recentBookAdapter;
    private ValueEventListener booksListener;
    private ValueEventListener issuedBooksListener;
    private ValueEventListener requestsListener;
    private ValueEventListener librariesListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sessionManager = new SessionManager(requireContext());

        loadUserData();



        binding.cardHomeSearch.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });

        binding.btnHomeScanQr.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.smartlibrary.activities.QrScannerActivity.class));
        });

        // Click listeners for stat cards
        binding.cardAvailableBooks.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });

        binding.cardIssuedBooks.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new IssuedBooksFragment(), "Issued Books");
            }
        });

        binding.cardPendingRequests.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new PendingRequestsFragment(), "Pending Requests");
            }
        });

        binding.cardLibraryLocations.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new LibraryLocationsFragment(), "Library Locations");
            }
        });

        binding.tvViewAllBooks.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });

        binding.cardAiAssistant.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.smartlibrary.activities.AiAssistantActivity.class));
        });

        setupCategories();
        setupRecentBooks();
        loadDashboardStats();
    }

    private void setupCategories() {
        List<String> categories = Arrays.asList(
                "Personal Development", "Philosophy & Religion", "Business",
                "Psychology", "Marketing", "Fiction", "Parenting & Education"
        );
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, category -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });
        binding.rvHomeCategories.setAdapter(categoryAdapter);
    }

    private void setupRecentBooks() {
        recentBookAdapter = new BookAdapter(book -> {
            Intent intent = new Intent(requireContext(), BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.getBookId());
            startActivity(intent);
        });
        binding.rvRecentBooks.setAdapter(recentBookAdapter);
    }

    private void loadDashboardStats() {
        String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";

        booksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                long totalAvailableCopies = 0;
                List<Book> books = new java.util.ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Book b = data.getValue(Book.class);
                    if (b != null) {
                        books.add(b);
                        totalAvailableCopies += Math.max(0, b.getAvailableCopies());
                    }
                }
                binding.tvStatAvailableCount.setText(totalAvailableCopies + " Available");
                
                // Sort by updated at (or fallback to original order), taking first 6
                books.sort((b1, b2) -> Long.compare(b2.getUpdatedAt(), b1.getUpdatedAt()));
                if (books.size() > 6) {
                    books = books.subList(0, 6);
                }
                recentBookAdapter.setBooks(books);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        mDatabase.child("books").addValueEventListener(booksListener);

        issuedBooksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                long activeCount = 0;
                DataSnapshot firstActive = null;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String status = data.child("status").getValue(String.class);
                    if ("Active".equalsIgnoreCase(status)) {
                        activeCount++;
                        if (firstActive == null) firstActive = data;
                    }
                }
                binding.tvStatIssuedCount.setText(activeCount + " Borrowed");
                
                if (firstActive != null) {
                    binding.layoutContinueReading.setVisibility(View.VISIBLE);
                    binding.layoutDashboardOverview.setVisibility(View.GONE);
                    String title = firstActive.child("bookTitle").getValue(String.class);
                    String author = firstActive.child("bookAuthor").getValue(String.class);
                    String cover = firstActive.child("bookCoverImage").getValue(String.class);
                    final String bookId = firstActive.child("bookId").getValue(String.class);
                    binding.tvContinueTitle.setText(title != null ? title : "Unknown Book");
                    binding.tvContinueAuthor.setText(author != null ? author : "Unknown Author");
                    if (cover != null && !cover.trim().isEmpty()) {
                        Glide.with(HomeFragment.this).load(cover).placeholder(R.drawable.ic_book).into(binding.imgContinueCover);
                    }
                    
                    binding.cardContinueReading.setOnClickListener(v -> {
                        if (bookId != null) {
                            Intent intent = new Intent(requireContext(), BookDetailActivity.class);
                            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, bookId);
                            startActivity(intent);
                        }
                    });
                } else {
                    binding.layoutContinueReading.setVisibility(View.GONE);
                    binding.layoutDashboardOverview.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        mDatabase.child("issuedBooks").orderByChild("userId").equalTo(userId).addValueEventListener(issuedBooksListener);

        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                long count = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String status = data.child("status").getValue(String.class);
                    if ("PENDING".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
                        count++;
                    }
                }
                binding.tvStatPendingCount.setText(count + " Requests");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        mDatabase.child("bookRequests").orderByChild("userId").equalTo(userId).addValueEventListener(requestsListener);

        librariesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                binding.tvStatLocationsCount.setText(snapshot.getChildrenCount() + " Branches");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        mDatabase.child("libraries").addValueEventListener(librariesListener);
    }

    private void loadUserData() {
        User user = sessionManager.getUserSession();
        if (user != null && binding != null) {
            binding.tvHomeUserName.setText(user.getName());
            if (user.getProfileImage() != null && !user.getProfileImage().trim().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.imgHomeProfile);
            } else {
                binding.imgHomeProfile.setImageResource(R.drawable.ic_profile);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
        loadDashboardStats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDatabase != null) {
            if (booksListener != null) mDatabase.child("books").removeEventListener(booksListener);
            if (issuedBooksListener != null) {
                String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
                mDatabase.child("issuedBooks").orderByChild("userId").equalTo(userId).removeEventListener(issuedBooksListener);
            }
            if (requestsListener != null) {
                String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
                mDatabase.child("bookRequests").orderByChild("userId").equalTo(userId).removeEventListener(requestsListener);
            }
            if (librariesListener != null) mDatabase.child("libraries").removeEventListener(librariesListener);
        }
        binding = null;
    }
}
