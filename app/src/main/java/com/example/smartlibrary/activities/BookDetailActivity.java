package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.smartlibrary.R;
import com.example.smartlibrary.database.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import com.example.smartlibrary.databinding.ActivityBookDetailsBinding;
import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_ID = "extra_book_id";

    private ActivityBookDetailsBinding binding;
    private DatabaseReference mDatabase;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Book currentBook;
    private ValueEventListener bookListener;
    private DatabaseReference bookRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        setSupportActionBar(binding.toolbarDetails);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbarDetails.setNavigationOnClickListener(v -> finish());

        String bookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        if (bookId != null) {
            bookRef = mDatabase.child("books").child(bookId);
            bookListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentBook = snapshot.getValue(Book.class);
                        if (currentBook != null) {
                            displayBookDetails();
                        } else {
                            Toast.makeText(BookDetailActivity.this, "Failed to parse book data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(BookDetailActivity.this, "Book details not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            };
            bookRef.addValueEventListener(bookListener);
        } else {
            Toast.makeText(this, "Book details not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnRequestBook.setOnClickListener(v -> showRequestConfirmationDialog());

        binding.btnAskAi.setOnClickListener(v -> {
            if (currentBook != null) {
                Intent aiIntent = new Intent(this, AiAssistantActivity.class);
                String context = "Title: " + currentBook.getTitle() + "\n" +
                                 "Author: " + currentBook.getAuthor() + "\n" +
                                 "Category: " + currentBook.getCategory() + "\n" +
                                 "Description: " + currentBook.getDescription();
                aiIntent.putExtra("BOOK_CONTEXT", context);
                startActivity(aiIntent);
            } else {
                startActivity(new Intent(this, AiAssistantActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookRef != null && bookListener != null) {
            bookRef.removeEventListener(bookListener);
        }
    }

    private void displayBookDetails() {
        binding.tvDetailsTitle.setText(currentBook.getTitle());
        binding.tvDetailsAuthor.setText("by " + currentBook.getAuthor());
        binding.tvDetailsCategory.setText(currentBook.getCategory());
        binding.tvDetailsRating.setText(String.format(Locale.getDefault(), "★ %.1f Rating", currentBook.getRating()));
        binding.tvDetailsDescription.setText(currentBook.getDescription());
        binding.tvDetailsIsbn.setText("ISBN: " + currentBook.getIsbn());
        binding.tvDetailsPublisher.setText("Publisher: " + currentBook.getPublisher() + " (" + currentBook.getPublicationYear() + ")");
        binding.tvDetailsCopies.setText("Total Copies: " + currentBook.getTotalCopies() + " | Available: " + currentBook.getAvailableCopies());
        binding.tvDetailsLocation.setText("Location: " + currentBook.getLibraryId());

        if (currentBook.getEffectiveCoverImage() != null && !currentBook.getEffectiveCoverImage().trim().isEmpty()) {
            Glide.with(this)
                    .load(currentBook.getEffectiveCoverImage())
                    .placeholder(R.drawable.ic_book)
                    .into(binding.imgDetailsCover);
        } else {
            binding.imgDetailsCover.setImageResource(R.drawable.ic_book);
        }

        if (currentBook.getAvailableCopies() <= 0) {
            binding.btnRequestBook.setEnabled(true);
            binding.btnRequestBook.setText("NOTIFY ME WHEN AVAILABLE");
            binding.btnRequestBook.setOnClickListener(v -> setupAvailabilityAlert());
        } else {
            binding.btnRequestBook.setEnabled(true);
            binding.btnRequestBook.setText("REQUEST BOOK");
            binding.btnRequestBook.setOnClickListener(v -> showRequestConfirmationDialog());
        }
        
        // Smart location
        StringBuilder smartLoc = new StringBuilder();
        if (currentBook.getFloor() != null) smartLoc.append("Floor: ").append(currentBook.getFloor()).append(" ");
        if (currentBook.getRoom() != null) smartLoc.append("Room: ").append(currentBook.getRoom()).append(" ");
        if (currentBook.getSection() != null) smartLoc.append("Section: ").append(currentBook.getSection()).append(" ");
        if (currentBook.getShelf() != null) smartLoc.append("Shelf: ").append(currentBook.getShelf()).append(" ");
        if (currentBook.getShelfNumber() != null) smartLoc.append("#").append(currentBook.getShelfNumber());
        
        if (smartLoc.length() > 0) {
            binding.tvSmartLocation.setText(smartLoc.toString().trim());
            binding.tvSmartLocation.setVisibility(View.VISIBLE);
        }
        
        // We will show QR via menu instead of inline.
        
        // Fetch Library details
        mDatabase.child("libraries").child(currentBook.getLibraryId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    com.example.smartlibrary.models.LibraryLocation lib = snapshot.getValue(com.example.smartlibrary.models.LibraryLocation.class);
                    if (lib != null) {
                        binding.tvDetailsLocation.setText("Location: " + lib.getName());
                        binding.tvDetailsAddress.setText(lib.getAddress());
                        binding.tvDetailsAddress.setVisibility(View.VISIBLE);
                        
                        binding.btnGetDirections.setVisibility(View.VISIBLE);
                        binding.btnGetDirections.setOnClickListener(v -> {
                            android.net.Uri gmmIntentUri = android.net.Uri.parse("google.navigation:q=" + lib.getLatitude() + "," + lib.getLongitude());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            try {
                                startActivity(mapIntent);
                            } catch (Exception ex) {
                                android.net.Uri geoUri = android.net.Uri.parse("geo:" + lib.getLatitude() + "," + lib.getLongitude() + "?q=" + lib.getLatitude() + "," + lib.getLongitude() + "(" + android.net.Uri.encode(lib.getName()) + ")");
                                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                                startActivity(fallbackIntent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_show_qr) {
            showBookQrDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBookQrDialog() {
        if (currentBook == null) return;
        try {
            android.graphics.Bitmap qrBitmap = com.example.smartlibrary.utils.QRCodeUtils.generateQRCode(currentBook.getBookId(), 500, 500);
            if (qrBitmap != null) {
                android.widget.ImageView imageView = new android.widget.ImageView(this);
                imageView.setImageBitmap(qrBitmap);
                int padding = 40;
                imageView.setPadding(padding, padding, padding, padding);
                
                new AlertDialog.Builder(this)
                        .setTitle("Book QR Code")
                        .setView(imageView)
                        .setPositiveButton("Close", null)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRequestConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Request Book")
                .setMessage("Would you like to place a borrowing request for '" + currentBook.getTitle() + "'?")
                .setPositiveButton("Request Now", (dialog, which) -> submitBookRequest())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupAvailabilityAlert() {
        User user = sessionManager.getUserSession();
        if (user == null) {
            Toast.makeText(this, "Please log in to set an alert", Toast.LENGTH_SHORT).show();
            return;
        }

        String alertId = "ALERT_" + currentBook.getBookId() + "_" + user.getUserId();
        
        java.util.Map<String, Object> alertData = new java.util.HashMap<>();
        alertData.put("alertId", alertId);
        alertData.put("bookId", currentBook.getBookId());
        alertData.put("userId", user.getUserId());
        alertData.put("createdAt", System.currentTimeMillis());
        alertData.put("status", "ACTIVE");

        mDatabase.child("availabilityAlerts").child(alertId).setValue(alertData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "You will be notified when this book becomes available.", Toast.LENGTH_LONG).show();
                        binding.btnRequestBook.setEnabled(false);
                        binding.btnRequestBook.setText("ALERT SET");
                    } else {
                        Toast.makeText(this, "Failed to set alert.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitBookRequest() {
        User user = sessionManager.getUserSession();
        if (user == null) {
            Toast.makeText(this, "Please log in to request books", Toast.LENGTH_SHORT).show();
            return;
        }

        BookRequest request = new BookRequest();
        request.setRequestId("REQ_" + System.currentTimeMillis());
        request.setUserId(user.getUserId());
        request.setUserName(user.getName());
        request.setBookId(currentBook.getBookId());
        request.setBookTitle(currentBook.getTitle());
        request.setBookAuthor(currentBook.getAuthor());
        request.setBookCoverImage(currentBook.getCoverImage());
        request.setStatus("APPROVED"); // Auto-approve for demo pass generation
        request.setRequestedAt(System.currentTimeMillis());
        request.setApprovedAt(System.currentTimeMillis());
        request.setExpiresAt(System.currentTimeMillis() + (24L * 3600 * 1000));
        request.setQrToken("QR_" + request.getRequestId() + "_" + currentBook.getBookId());

        mDatabase.child("bookRequests").orderByChild("userId").equalTo(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean alreadyRequested = false;
                for (DataSnapshot data : snapshot.getChildren()) {
                    BookRequest existing = data.getValue(BookRequest.class);
                    if (existing != null && existing.getBookId().equals(currentBook.getBookId()) && "APPROVED".equals(existing.getStatus())) {
                        alreadyRequested = true;
                        break;
                    }
                }
                
                if (alreadyRequested) {
                    Toast.makeText(BookDetailActivity.this, "You already have an active request for this book.", Toast.LENGTH_LONG).show();
                } else {
                    mDatabase.child("bookRequests").child(request.getRequestId()).setValue(request).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(BookDetailActivity.this, "Request approved! QR Pass generated.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(BookDetailActivity.this, QrCodeActivity.class);
                            intent.putExtra(QrCodeActivity.EXTRA_REQUEST_ID, request.getRequestId());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(BookDetailActivity.this, "Failed to submit request.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
