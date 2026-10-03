package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.R;
import com.example.smartlibrary.utils.SessionManager;

public class OnboardingActivity extends AppCompatActivity {

    private int currentStep = 0;
    
    private final String[] titles = {
        "Explore Your Interests",
        "Expand Your Hobbies",
        "AI Librarian Assistant"
    };
    
    private final String[] descriptions = {
        "Discover thousands of premium books carefully curated for your academic and personal growth.",
        "From fiction to business, find resources to fuel your passions and hobbies.",
        "Get smart recommendations and instant answers from our cutting-edge AI assistant."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        SessionManager sessionManager = new SessionManager(this);
        
        // If already completed onboarding, go straight to login
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tvOnboardingTitle);
        TextView tvDesc = findViewById(R.id.tvOnboardingDesc);
        View dot1 = findViewById(R.id.dot1);
        View dot2 = findViewById(R.id.dot2);
        View dot3 = findViewById(R.id.dot3);
        TextView btnSkip = findViewById(R.id.btnSkip);
        Button btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            if (currentStep < 2) {
                currentStep++;
                tvTitle.setText(titles[currentStep]);
                tvDesc.setText(descriptions[currentStep]);
                
                dot1.setAlpha(currentStep == 0 ? 1.0f : 0.3f);
                dot2.setAlpha(currentStep == 1 ? 1.0f : 0.3f);
                dot3.setAlpha(currentStep == 2 ? 1.0f : 0.3f);
                
                if (currentStep == 2) {
                    btnNext.setText("GET STARTED");
                }
            } else {
                goToLogin();
            }
        });

        btnSkip.setOnClickListener(v -> goToLogin());
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
