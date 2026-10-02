package com.example.smartlibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smartlibrary.adapters.BookAdapter;
import com.example.smartlibrary.databinding.ActivityLibraryDetailsBinding;
import com.example.smartlibrary.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LibraryDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_LIBRARY_ID = "extra_library_id";
    public static final String EXTRA_LIBRARY_NAME = "extra_library_name";
    public static final String EXTRA_LIBRARY_ADDRESS = "extra_library_address";
    public static final String EXTRA_LIBRARY_HOURS = "extra_library_hours";
    public static final String EXTRA_LIBRARY_PHONE = "extra_library_phone";
    public static final String EXTRA_LIBRARY_LAT = "extra_library_lat";
    public static final String EXTRA_LIBRARY_LNG = "extra_library_lng";
    public static final String EXTRA_DISTANCE = "extra_distance";

    private ActivityLibraryDetailsBinding binding;
    private DatabaseReference mDatabase;
    private BookAdapter bookAdapter;
    private List<Book> allBooks = new ArrayList<>();
    private String libraryId = "";
    private String searchQuery = "";
    private double lat = 0.0;
    private double lng = 0.0;
    private String name = "";
    private String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("books");

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra(EXTRA_LIBRARY_ID)) {
            libraryId = getIntent().getStringExtra(EXTRA_LIBRARY_ID);
            name = getIntent().getStringExtra(EXTRA_LIBRARY_NAME);
            address = getIntent().getStringExtra(EXTRA_LIBRARY_ADDRESS);
            lat = getIntent().getDoubleExtra(EXTRA_LIBRARY_LAT, 0.0);
            lng = getIntent().getDoubleExtra(EXTRA_LIBRARY_LNG, 0.0);

            binding.tvLibName.setText(name);
            binding.tvLibAddress.setText(address);
            binding.tvLibHours.setText(getIntent().getStringExtra(EXTRA_LIBRARY_HOURS));
            binding.tvLibPhone.setText(getIntent().getStringExtra(EXTRA_LIBRARY_PHONE));
            
            String dist = getIntent().getStringExtra(EXTRA_DISTANCE);
            if (dist != null && !dist.isEmpty()) {
                binding.tvLibDistance.setText(dist);
                binding.tvLibDistance.setVisibility(View.VISIBLE);
            } else {
                binding.tvLibDistance.setVisibility(View.GONE);
            }
        }

        binding.btnGetDirections.setOnClickListener(v -> openExternalMapsDirections());

        setupRecyclerView();
        setupSearchView();
        loadBooksForLibrary();
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(book -> {
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.getBookId());
            startActivity(intent);
        });
        binding.rvBooks.setAdapter(bookAdapter);
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

    private void loadBooksForLibrary() {
        mDatabase.orderByChild("libraryId").equalTo(libraryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allBooks.clear();
                int totalAvailable = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    Book book = data.getValue(Book.class);
                    if (book != null) {
                        allBooks.add(book);
                        totalAvailable += book.getAvailableCopies();
                    }
                }
                binding.tvTotalBooksCount.setText(allBooks.size() + " unique books, " + totalAvailable + " copies available");
                filterBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LibraryDetailsActivity.this, "Failed to load books.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterBooks() {
        List<Book> filtered = new ArrayList<>();
        String q = searchQuery.toLowerCase().trim();
        for (Book b : allBooks) {
            boolean matchQuery = q.isEmpty() || (b.getTitle() != null && b.getTitle().toLowerCase().contains(q))
                    || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q));

            if (matchQuery) {
                filtered.add(b);
            }
        }

        if (filtered.isEmpty()) {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
            binding.rvBooks.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
            binding.rvBooks.setVisibility(View.VISIBLE);
            bookAdapter.setBooks(filtered);
        }
    }

    private void openExternalMapsDirections() {
        try {
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + Uri.encode(name + ", " + address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Intent genericIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng));
                startActivity(genericIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not open map navigation.", Toast.LENGTH_SHORT).show();
        }
    }
}
