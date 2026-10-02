package com.example.smartlibrary.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.database.FirebaseManager;
import com.example.smartlibrary.databinding.ActivityForgotPasswordBinding;

import java.util.Locale;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseManager firebaseManager;
    private DatabaseHelper databaseHelper;
    private CountDownTimer timer;
    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance(this);
        databaseHelper = DatabaseHelper.getInstance(this);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSendOtp.setOnClickListener(v -> sendOtpCode());

        binding.btnResetPassword.setOnClickListener(v -> verifyOtpAndResetPassword());
    }

    private void sendOtpCode() {
        userEmail = binding.etForgotEmail.getText() != null ? binding.etForgotEmail.getText().toString().trim() : "";

        if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.tilForgotEmail.setError("Valid registered email is required.");
            return;
        } else binding.tilForgotEmail.setError(null);

        setLoading(true);

        firebaseManager.sendPasswordResetEmail(userEmail, new FirebaseManager.ActionCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this, "OTP Verification code sent to " + userEmail, Toast.LENGTH_LONG).show();
                binding.cardStepEmail.setVisibility(View.GONE);
                binding.cardStepOtp.setVisibility(View.VISIBLE);
                startOtpTimer();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startOtpTimer() {
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                binding.tvOtpTimer.setText(String.format(Locale.getDefault(), "Resend OTP in 00:%02d", seconds));
            }

            @Override
            public void onFinish() {
                binding.tvOtpTimer.setText("Resend OTP");
                binding.tvOtpTimer.setOnClickListener(v -> sendOtpCode());
            }
        }.start();
    }

    private void verifyOtpAndResetPassword() {
        String otp = binding.etOtp.getText() != null ? binding.etOtp.getText().toString().trim() : "";
        String newPass = binding.etNewPassword.getText() != null ? binding.etNewPassword.getText().toString().trim() : "";
        String confirmPass = binding.etConfirmNewPassword.getText() != null ? binding.etConfirmNewPassword.getText().toString().trim() : "";

        if (otp.length() < 4) {
            binding.tilOtp.setError("Please enter the verification code");
            return;
        } else binding.tilOtp.setError(null);

        if (newPass.length() < 6) {
            binding.tilNewPassword.setError("New password must be at least 6 characters");
            return;
        } else binding.tilNewPassword.setError(null);

        if (!newPass.equals(confirmPass)) {
            binding.tilConfirmNewPassword.setError("Passwords do not match");
            return;
        } else binding.tilConfirmNewPassword.setError(null);

        databaseHelper.updatePassword(userEmail, newPass);
        Toast.makeText(this, "Password updated successfully! Please log in.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBarForgot.setVisibility(View.VISIBLE);
            binding.btnSendOtp.setEnabled(false);
        } else {
            binding.progressBarForgot.setVisibility(View.GONE);
            binding.btnSendOtp.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
