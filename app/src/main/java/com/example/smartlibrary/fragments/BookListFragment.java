package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.BookDetailActivity;
import com.example.smartlibrary.adapters.BookAdapter;
import com.example.smartlibrary.adapters.CategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentBooksBinding;
import com.example.smartlibrary.models.Book;

import java.util.Arrays;
import java.util.List;

public class BookListFragment extends Fragment {

    private FragmentBooksBinding binding;
    private DatabaseReference mDatabase;
    private BookAdapter bookAdapter;
    private List<Book> allBooks = new java.util.ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private ValueEventListener booksListener;

    private String selectedCategory = "All";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("books");

        if (getArguments() != null) {
            String q = getArguments().getString("searchQuery");
            if (q != null) {
                searchQuery = q;
            }
        }
        
        setupCategoryChips();
        setupRecyclerView();
        setupSearchView();
        
        if (!searchQuery.isEmpty()) {
            binding.searchViewBooks.setQuery(searchQuery, false);
        }

        loadBooks();
    }

    private void setupCategoryChips() {
        List<String> categories = Arrays.asList(
                "All", "Personal Development", "Philosophy & Religion", "Business",
                "Psychology", "Marketing", "Fiction", "Parenting & Education",
                "Finance & Investments", "Nature & Science", "History & Politics"
        );
        categoryAdapter = new CategoryAdapter(categories, category -> {
            selectedCategory = category;
            filterBooks();
        });
        binding.rvCategoryChips.setAdapter(categoryAdapter);
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(book -> {
            Intent intent = new Intent(requireContext(), BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.getBookId());
            startActivity(intent);
        });
        binding.rvBookCatalog.setAdapter(bookAdapter);
    }

    private void setupSearchView() {
        binding.searchViewBooks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                filterBooks();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                filterBooks();
                return true;
            }
        });
    }

    private void loadBooks() {
        if (binding == null) return;
        binding.rvBookCatalog.setVisibility(View.GONE);
        binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
        
        booksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                allBooks.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Book book = data.getValue(Book.class);
                    if (book != null) allBooks.add(book);
                }
                filterBooks();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (binding == null) return;
                binding.rvBookCatalog.setVisibility(View.GONE);
                binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
                binding.layoutEmpty.tvEmptyDescription.setText("Failed to load books. Tap to retry.");
                binding.layoutEmpty.emptyStateContainer.setOnClickListener(v -> loadBooks());
            }
        };
        mDatabase.addValueEventListener(booksListener);
    }

    private void filterBooks() {
        if (binding == null) return;
        List<Book> filtered = new java.util.ArrayList<>();
        String q = searchQuery.toLowerCase().trim();
        for (Book b : allBooks) {
            boolean matchCategory = selectedCategory.equals("All") || selectedCategory.equalsIgnoreCase(b.getCategory());
            boolean matchQuery = q.isEmpty() || (b.getTitle() != null && b.getTitle().toLowerCase().contains(q)) 
                    || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q))
                    || (b.getShelf() != null && b.getShelf().toLowerCase().equals(q));
            
            if (q.startsWith("shelf_") && b.getShelf() != null && b.getShelf().toLowerCase().equals(q)) {
                matchQuery = true;
            }
            
            if (matchCategory && matchQuery) {
                filtered.add(b);
            }
        }

        if (filtered.isEmpty()) {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
            binding.layoutEmpty.tvEmptyDescription.setText("No books found matching your criteria.");
            binding.layoutEmpty.emptyStateContainer.setOnClickListener(null);
            binding.rvBookCatalog.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
            binding.rvBookCatalog.setVisibility(View.VISIBLE);
            bookAdapter.setBooks(filtered);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDatabase != null && booksListener != null) {
            mDatabase.removeEventListener(booksListener);
        }
        binding = null;
    }
}
