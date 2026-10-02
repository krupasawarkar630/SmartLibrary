package com.example.smartlibrary.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityQrCodeBinding;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.utils.DateUtils;
import com.example.smartlibrary.utils.QRCodeUtils;

public class QrCodeActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST_ID = "extra_request_id";

    private ActivityQrCodeBinding binding;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("bookRequests");

        binding.btnBackQr.setOnClickListener(v -> finish());

        String requestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        if (requestId != null) {
            mDatabase.child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        BookRequest request = snapshot.getValue(BookRequest.class);
                        if (request != null) {
                            displayQrPass(request);
                        } else {
                            Toast.makeText(QrCodeActivity.this, "Request pass not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(QrCodeActivity.this, "Request pass not found", Toast.LENGTH_SHORT).show();
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
    }

    private void displayQrPass(BookRequest request) {
        binding.tvQrBookTitle.setText(request.getBookTitle());
        binding.tvQrUserName.setText("Student: " + request.getUserName());
        binding.tvQrRequestId.setText("Request ID: " + request.getRequestId());
        binding.tvQrExpiry.setText("Expires: " + DateUtils.formatDateTime(request.getExpiresAt()));

        String tokenPayload = QRCodeUtils.createSecureTokenPayload(
                request.getRequestId(), request.getBookId(), request.getUserId(), request.getExpiresAt()
        );

        Bitmap qrBitmap = QRCodeUtils.generateQRCode(tokenPayload, 500, 500);
        if (qrBitmap != null) {
            binding.imgQrCode.setImageBitmap(qrBitmap);
        }
    }
}
