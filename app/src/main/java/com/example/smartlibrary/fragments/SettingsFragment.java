package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.ForgotPasswordActivity;
import com.example.smartlibrary.activities.MainActivity;
import com.example.smartlibrary.databinding.FragmentSettingsBinding;
import com.example.smartlibrary.utils.SessionManager;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        binding.switchDarkMode.setChecked(sessionManager.isDarkMode());
        binding.switchNotifications.setChecked(sessionManager.isNotificationsEnabled());

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setNotificationsEnabled(isChecked);
            Toast.makeText(requireContext(), "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        binding.btnChangePasswordSettings.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ForgotPasswordActivity.class));
        });

        binding.btnPrivacyPolicy.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "QR Smart Library Privacy Policy: Your data is protected.", Toast.LENGTH_LONG).show();
        });

        binding.btnTerms.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Terms & Conditions: Standard university library borrowing rules apply.", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
