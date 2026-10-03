package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.smartlibrary.R;
import com.example.smartlibrary.databinding.ActivitySplashBinding;
import com.example.smartlibrary.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Add Splash Animation (Scale & Alpha for whole view)
        AnimationSet animationSet = new AnimationSet(true);
        
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(800);
        animationSet.addAnimation(alpha);
        
        ScaleAnimation scale = new ScaleAnimation(
                0.9f, 1.0f, 0.9f, 1.0f, 
                Animation.RELATIVE_TO_SELF, 0.5f, 
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(800);
        animationSet.addAnimation(scale);
        
        binding.getRoot().startAnimation(animationSet);
        
        // Start the Animated Vector Drawable for the logo
        android.widget.ImageView logoImage = findViewById(R.id.imgAnimatedLogo);
        if (logoImage != null && logoImage.getDrawable() instanceof android.graphics.drawable.AnimatedVectorDrawable) {
            ((android.graphics.drawable.AnimatedVectorDrawable) logoImage.getDrawable()).start();
        }

        sessionManager = new SessionManager(this);

        // Apply saved dark mode theme
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2000); // Increased slightly for animation time
    }
}
