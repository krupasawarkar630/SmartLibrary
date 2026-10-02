package com.example.smartlibrary.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends AppCompatActivity {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackAbout.setOnClickListener(v -> finish());
    }
}
