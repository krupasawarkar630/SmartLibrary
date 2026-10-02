package com.example.smartlibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.smartlibrary.R;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityEditProfileBinding;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private User currentUser;
    private String selectedPhotoUri = "";

    private final ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception ignored) {}
                        selectedPhotoUri = imageUri.toString();
                        displayAvatar(selectedPhotoUri);
                        Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        dbHelper = DatabaseHelper.getInstance(this);

        binding.btnBackEditProfile.setOnClickListener(v -> finish());

        currentUser = sessionManager.getUserSession();
        if (currentUser != null) {
            binding.etEditName.setText(currentUser.getName());
            binding.etEditMobile.setText(currentUser.getMobile());
            binding.etEditEmailReadOnly.setText(currentUser.getEmail());
            binding.etEditRollReadOnly.setText(currentUser.getRollNumber());

            selectedPhotoUri = currentUser.getProfileImage() != null ? currentUser.getProfileImage() : "";
            displayAvatar(selectedPhotoUri);
        }

        binding.btnChangePhoto.setOnClickListener(v -> openGalleryForPhoto());

        binding.btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
    }

    private void openGalleryForPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        photoPickerLauncher.launch(intent);
    }

    private void displayAvatar(String imageUriStr) {
        if (imageUriStr != null && !imageUriStr.trim().isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(imageUriStr))
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(binding.imgEditProfileAvatar);
        } else {
            binding.imgEditProfileAvatar.setImageResource(R.drawable.ic_profile);
        }
    }

    private void saveProfileChanges() {
        String name = binding.etEditName.getText() != null ? binding.etEditName.getText().toString().trim() : "";
        String mobile = binding.etEditMobile.getText() != null ? binding.etEditMobile.getText().toString().trim() : "";

        if (name.isEmpty()) {
            binding.tilEditName.setError("Full name is required");
            return;
        } else binding.tilEditName.setError(null);

        if (mobile.isEmpty()) {
            binding.tilEditMobile.setError("Mobile number is required");
            return;
        } else binding.tilEditMobile.setError(null);

        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setMobile(mobile);
            currentUser.setProfileImage(selectedPhotoUri);

            dbHelper.updateUserProfile(currentUser);
            sessionManager.saveUserSession(currentUser);

            Toast.makeText(this, "Profile changes saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
