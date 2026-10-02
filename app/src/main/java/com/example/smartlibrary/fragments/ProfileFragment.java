package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.smartlibrary.R;
import com.example.smartlibrary.activities.EditProfileActivity;
import com.example.smartlibrary.activities.ForgotPasswordActivity;
import com.example.smartlibrary.activities.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.activities.EditProfileActivity;
import com.example.smartlibrary.activities.ForgotPasswordActivity;
import com.example.smartlibrary.activities.MainActivity;
import com.example.smartlibrary.databinding.FragmentProfileBinding;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        displayUserProfile();

        binding.btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ForgotPasswordActivity.class));
        });

        binding.btnLogoutProfile.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).confirmLogout();
            }
        });
    }

    private void displayUserProfile() {
        User user = sessionManager.getUserSession();
        if (user != null) {
            binding.tvProfileName.setText(user.getName());
            binding.tvProfileEmail.setText(user.getEmail());
            binding.tvProfileRoll.setText("Roll: " + user.getRollNumber());
            binding.tvProfilePhone.setText(user.getMobile());
            binding.tvProfileRole.setText(user.getRole() != null ? user.getRole().toUpperCase() + " MEMBER" : "STUDENT MEMBER");

            // Profile photo rendering
            if (user.getProfileImage() != null && !user.getProfileImage().trim().isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(user.getProfileImage()))
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.imgProfileAvatar);
            } else {
                binding.imgProfileAvatar.setImageResource(R.drawable.ic_profile);
            }

            // Expanded Account & Membership Details
            binding.tvProfileDepartment.setText("Computer Science & Engineering");
            binding.tvProfileMembershipStatus.setText("Active Smart Pass");
            binding.tvProfileBorrowLimit.setText("5 Books Max Limit");

            // Query DB for live stats
            mDatabase.child("issuedBooks").orderByChild("userId").equalTo(user.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (binding == null) return;
                    int activeLoans = 0;
                    double totalFine = 0.0;
                    
                    for (DataSnapshot data : snapshot.getChildren()) {
                        IssuedBook b = data.getValue(IssuedBook.class);
                        if (b != null && "Active".equals(b.getStatus())) {
                            activeLoans++;
                            if (System.currentTimeMillis() > b.getDueDate()) {
                                long diff = System.currentTimeMillis() - b.getDueDate();
                                long overdueDays = (diff / (1000 * 60 * 60 * 24)) + 1;
                                totalFine += overdueDays * 1.50;
                            }
                        }
                    }

                    binding.tvProfileActiveLoans.setText(activeLoans + " Book(s) Borrowed");
                    
                    if (totalFine > 0) {
                        binding.tvProfileFineStanding.setText(String.format(Locale.getDefault(), "$%.2f (Overdue Fine Pending)", totalFine));
                        binding.tvProfileFineStanding.setTextColor(requireContext().getColor(R.color.danger));
                    } else {
                        binding.tvProfileFineStanding.setText("$0.00 (Clean Standing)");
                        binding.tvProfileFineStanding.setTextColor(requireContext().getColor(R.color.success));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserProfile();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateNavHeader();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
