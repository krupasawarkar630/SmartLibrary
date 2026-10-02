package com.example.smartlibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.R;
import com.example.smartlibrary.database.FirebaseManager;
import com.example.smartlibrary.databinding.ActivityMainBinding;
import com.example.smartlibrary.databinding.NavHeaderMainBinding;
import com.example.smartlibrary.fragments.BookListFragment;
import com.example.smartlibrary.fragments.FeedbackFragment;
import com.example.smartlibrary.fragments.HistoryFragment;
import com.example.smartlibrary.fragments.HomeFragment;
import com.example.smartlibrary.fragments.IssuedBooksFragment;
import com.example.smartlibrary.fragments.LearningPointsFragment;
import com.example.smartlibrary.fragments.LibraryLocationsFragment;
import com.example.smartlibrary.fragments.NotificationsFragment;
import com.example.smartlibrary.fragments.PendingRequestsFragment;
import com.example.smartlibrary.fragments.ProfileFragment;
import com.example.smartlibrary.fragments.QuizFragment;
import com.example.smartlibrary.fragments.SettingsFragment;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
        
        com.example.smartlibrary.database.FirebaseManager.getInstance(this).seedFirebaseData();

        // Bottom Navigation Listener
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment(), "Home");
                return true;
            } else if (itemId == R.id.nav_books) {
                loadFragment(new BookListFragment(), "Available Books");
                return true;
            } else if (itemId == R.id.nav_issued) {
                loadFragment(new IssuedBooksFragment(), "Issued Books");
                return true;
            } else if (itemId == R.id.nav_history) {
                loadFragment(new HistoryFragment(), "Borrowing History");
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment(), "User Profile");
                return true;
            }
            return false;
        });

        // Default Fragment
        if (savedInstanceState == null) {
            String shelfQuery = getIntent().getStringExtra("EXTRA_SHELF_QUERY");
            if (shelfQuery != null && !shelfQuery.isEmpty()) {
                BookListFragment fragment = new BookListFragment();
                Bundle args = new Bundle();
                args.putString("searchQuery", shelfQuery);
                fragment.setArguments(args);
                loadFragment(fragment, "Shelf Books: " + shelfQuery);
                binding.bottomNavigationView.setSelectedItemId(R.id.nav_books);
            } else {
                loadFragment(new HomeFragment(), "Home");
            }
        }
    }

    public void updateNavHeader() {
        View headerView = binding.navigationView.getHeaderView(0);
        if (headerView != null) {
            NavHeaderMainBinding headerBinding = NavHeaderMainBinding.bind(headerView);
            User user = sessionManager.getUserSession();
            if (user != null) {
                headerBinding.tvNavName.setText(user.getName());
                headerBinding.tvNavEmail.setText(user.getEmail());
                headerBinding.tvNavRollNo.setText("Roll No: " + user.getRollNumber());
            }
        }
    }

    public void loadFragment(Fragment fragment, String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_drawer_home) {
            loadFragment(new HomeFragment(), "Home");
            binding.bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (itemId == R.id.nav_drawer_profile) {
            loadFragment(new ProfileFragment(), "User Profile");
            binding.bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else if (itemId == R.id.nav_drawer_books) {
            loadFragment(new BookListFragment(), "Available Books");
            binding.bottomNavigationView.setSelectedItemId(R.id.nav_books);
        } else if (itemId == R.id.nav_drawer_issued) {
            loadFragment(new IssuedBooksFragment(), "Issued Books");
            binding.bottomNavigationView.setSelectedItemId(R.id.nav_issued);
        } else if (itemId == R.id.nav_drawer_pending) {
            loadFragment(new PendingRequestsFragment(), "Pending Requests");
        } else if (itemId == R.id.nav_drawer_history) {
            loadFragment(new HistoryFragment(), "Borrowing History");
            binding.bottomNavigationView.setSelectedItemId(R.id.nav_history);
        } else if (itemId == R.id.nav_drawer_learning) {
            loadFragment(new LearningPointsFragment(), "Learning Points");
        } else if (itemId == R.id.nav_drawer_locations) {
            loadFragment(new LibraryLocationsFragment(), "Library Locations");
        } else if (itemId == R.id.nav_drawer_quiz) {
            loadFragment(new QuizFragment(), "Library Quiz");
        } else if (itemId == R.id.nav_drawer_feedback) {
            loadFragment(new FeedbackFragment(), "Feedback");
        } else if (itemId == R.id.nav_drawer_notifications) {
            loadFragment(new NotificationsFragment(), "Notifications");
        } else if (itemId == R.id.nav_drawer_settings) {
            loadFragment(new SettingsFragment(), "Settings");
        } else if (itemId == R.id.nav_drawer_about) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (itemId == R.id.nav_drawer_contact) {
            startActivity(new Intent(this, ContactUsActivity.class));
        } else if (itemId == R.id.nav_drawer_rate) {
            rateApp();
        } else if (itemId == R.id.nav_drawer_share) {
            shareApp();
        } else if (itemId == R.id.nav_drawer_logout) {
            confirmLogout();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out QR Smart Library Management System app for Android!");
        startActivity(Intent.createChooser(shareIntent, "Share App"));
    }

    public void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out of QR Smart Library?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void performLogout() {
        FirebaseManager.getInstance(this).logout();
        sessionManager.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @android.annotation.SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
