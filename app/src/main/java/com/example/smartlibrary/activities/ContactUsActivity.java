package com.example.smartlibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.databinding.ActivityContactUsBinding;

public class ContactUsActivity extends AppCompatActivity {

    private ActivityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackContact.setOnClickListener(v -> finish());

        binding.btnContactCall.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+15552345678"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Could not launch phone dialer", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnContactEmail.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@smartlibrary.edu"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Smart Library Support Query");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnContactMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, LibraryMapActivity.class);
            startActivity(intent);
        });
    }
}
