package com.example.smartlibrary.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityQrScannerBinding;
import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.models.IssuedBook;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import org.json.JSONObject;

import java.util.List;

public class QrScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1002;
    private ActivityQrScannerBinding binding;
    private DatabaseReference mDatabase;
    private DatabaseHelper dbHelper;
    private boolean isScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        dbHelper = DatabaseHelper.getInstance(this);

        binding.btnBackScanner.setOnClickListener(v -> finish());

        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            startScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR passes.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startScanner() {
        binding.barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null && !isScanned) {
                    isScanned = true;
                    processScannedQrToken(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }

    private void processScannedQrToken(String rawText) {
        try {
            if (rawText == null || rawText.trim().isEmpty()) {
                Toast.makeText(this, "Empty QR code content.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String requestId = "";
            String bookId = "";
            String isbn = "";

            if (rawText.startsWith("{")) {
                JSONObject json = new JSONObject(rawText);
                requestId = json.optString("requestId");
                bookId = json.optString("bookId");
                isbn = json.optString("isbn");
                if (bookId.isEmpty()) bookId = json.optString("id");
            } else if (rawText.contains(":")) {
                String[] parts = rawText.split(":");
                if (parts.length >= 3) {
                    requestId = parts[0];
                    bookId = parts[1];
                }
            } else {
                bookId = rawText.trim();
            }

            final String fRequestId = requestId;
            final String fBookId = bookId;
            final String fIsbn = isbn;

            mDatabase.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (rawText.startsWith("SHELF_")) {
                        // Handle Shelf QR
                        Toast.makeText(QrScannerActivity.this, "Scanned Shelf: " + rawText, Toast.LENGTH_SHORT).show();
                        android.content.Intent intent = new android.content.Intent(QrScannerActivity.this, MainActivity.class);
                        intent.putExtra("EXTRA_SHELF_QUERY", rawText);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    
                    if (rawText.startsWith("LIB_")) {
                        // Handle Library Check-in QR
                        Toast.makeText(QrScannerActivity.this, "Checked into Library!", Toast.LENGTH_SHORT).show();
                        
                        // Record visit in Firebase
                        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null ? 
                                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid() : "unknown_user";
                        java.util.Map<String, Object> visitData = new java.util.HashMap<>();
                        visitData.put("userId", userId);
                        visitData.put("libraryId", rawText);
                        visitData.put("timestamp", System.currentTimeMillis());
                        mDatabase.child("libraryVisits").push().setValue(visitData);
                        
                        // Open Library Details
                        android.content.Intent intent = new android.content.Intent(QrScannerActivity.this, LibraryDetailsActivity.class);
                        intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_ID, rawText);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    
                    Book foundBook = null;
                    String searchCode = !fBookId.isEmpty() ? fBookId : (!fIsbn.isEmpty() ? fIsbn : rawText);
                    
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Book b = data.getValue(Book.class);
                        if (b != null && (searchCode.equals(b.getBookId()) || searchCode.equals(b.getIsbn()))) {
                            foundBook = b;
                            break;
                        }
                    }

                    if (foundBook != null) {
                        Toast.makeText(QrScannerActivity.this, "Book Found: " + foundBook.getTitle(), Toast.LENGTH_SHORT).show();
                        android.content.Intent intent = new android.content.Intent(QrScannerActivity.this, BookDetailActivity.class);
                        intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, foundBook.getBookId());
                        startActivity(intent);
                        finish();
                    } else if (!fRequestId.isEmpty()) {
                        mDatabase.child("bookRequests").child(fRequestId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot reqSnapshot) {
                                if (reqSnapshot.exists()) {
                                    BookRequest request = reqSnapshot.getValue(BookRequest.class);
                                    if (request != null && "APPROVED".equalsIgnoreCase(request.getStatus())) {
                                        IssuedBook issuedBook = new IssuedBook();
                                        issuedBook.setIssuedBookId("ISS_" + System.currentTimeMillis());
                                        issuedBook.setUserId(request.getUserId());
                                        issuedBook.setBookId(request.getBookId());
                                        issuedBook.setRequestId(request.getRequestId());
                                        issuedBook.setBookTitle(request.getBookTitle());
                                        issuedBook.setBookAuthor(request.getBookAuthor());
                                        issuedBook.setBookCoverImage(request.getBookCoverImage());
                                        issuedBook.setIssueDate(System.currentTimeMillis());
                                        issuedBook.setDueDate(System.currentTimeMillis() + (14L * 24 * 3600 * 1000));
                                        issuedBook.setStatus("Active");

                                        mDatabase.child("books").child(request.getBookId()).child("availableCopies").runTransaction(new com.google.firebase.database.Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData mutableData) {
                                                Integer count = mutableData.getValue(Integer.class);
                                                if (count == null || count <= 0) {
                                                    return com.google.firebase.database.Transaction.abort();
                                                }
                                                mutableData.setValue(count - 1);
                                                return com.google.firebase.database.Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                if (committed) {
                                                    mDatabase.child("issuedBooks").child(issuedBook.getIssuedBookId()).setValue(issuedBook);
                                                    mDatabase.child("bookRequests").child(fRequestId).child("status").setValue("COMPLETED");
                                                    Toast.makeText(QrScannerActivity.this, "Book Successfully Issued: " + request.getBookTitle(), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(QrScannerActivity.this, "Failed to issue book. No copies available.", Toast.LENGTH_LONG).show();
                                                }
                                                finish();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(QrScannerActivity.this, "Request pass not valid or already used.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(QrScannerActivity.this, "Request pass not found.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { finish(); }
                        });
                    } else {
                        Toast.makeText(QrScannerActivity.this, "No book or valid request pass found.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { finish(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing QR code: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.barcodeScannerView.pause();
    }
}
