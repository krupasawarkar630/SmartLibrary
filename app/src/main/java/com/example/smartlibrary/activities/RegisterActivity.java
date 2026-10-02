package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.database.FirebaseManager;
import com.example.smartlibrary.databinding.ActivityRegisterBinding;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SessionManager sessionManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        firebaseManager = FirebaseManager.getInstance(this);

        binding.btnRegister.setOnClickListener(v -> performRegistration());

        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String name = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String mobile = binding.etMobile.getText() != null ? binding.etMobile.getText().toString().trim() : "";
        String roll = binding.etRollNumber.getText() != null ? binding.etRollNumber.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString().trim() : "";

        if (name.isEmpty()) {
            binding.tilFullName.setError("Full Name is required");
            return;
        } else binding.tilFullName.setError(null);

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Valid email address is required");
            return;
        } else binding.tilEmail.setError(null);

        if (mobile.isEmpty()) {
            binding.tilMobile.setError("Mobile number is required");
            return;
        } else binding.tilMobile.setError(null);

        if (roll.isEmpty()) {
            binding.tilRollNumber.setError("Roll / Student ID number is required");
            return;
        } else binding.tilRollNumber.setError(null);

        if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            return;
        } else binding.tilPassword.setError(null);

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            return;
        } else binding.tilConfirmPassword.setError(null);

        setLoading(true);

        User newUser = new User(
                UUID.randomUUID().toString(),
                name, email, mobile, roll, password, "", "student", System.currentTimeMillis()
        );

        firebaseManager.registerUser(newUser, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                sessionManager.saveUserSession(user);
                Toast.makeText(RegisterActivity.this, "Account registered successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Registration Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBarRegister.setVisibility(View.VISIBLE);
            binding.btnRegister.setEnabled(false);
        } else {
            binding.progressBarRegister.setVisibility(View.GONE);
            binding.btnRegister.setEnabled(true);
        }
    }
}
