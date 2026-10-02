package com.example.smartlibrary.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.LibraryDetailsActivity;
import com.example.smartlibrary.adapters.LibraryAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentLibraryLocationsBinding;
import com.example.smartlibrary.models.LibraryLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LibraryLocationsFragment extends Fragment {

    private FragmentLibraryLocationsBinding binding;
    private DatabaseReference mDatabase;
    private LibraryAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryLocationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("libraries");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        adapter = new LibraryAdapter(new LibraryAdapter.OnLibraryClickListener() {
            @Override
            public void onCallClick(LibraryLocation location) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + location.getPhone()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Could not launch phone dialer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMapClick(LibraryLocation location) {
                Intent intent = new Intent(requireContext(), LibraryDetailsActivity.class);
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_ID, location.getId());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_NAME, location.getName());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_ADDRESS, location.getAddress());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_HOURS, location.getOpeningHours());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_PHONE, location.getPhone());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_LAT, location.getLatitude());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_LNG, location.getLongitude());
                if (location.getDistance() > 0) {
                    intent.putExtra(LibraryDetailsActivity.EXTRA_DISTANCE, String.format("%.1f km away", location.getDistance() / 1000f));
                }
                startActivity(intent);
            }
        });

        binding.rvLibraryLocations.setAdapter(adapter);

        fetchUserLocationAndLoadLibraries();
    }

    private void fetchUserLocationAndLoadLibraries() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(location -> {
                userLocation = location;
                loadLibraries();
            }).addOnFailureListener(e -> loadLibraries());
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocationAndLoadLibraries();
            } else {
                loadLibraries(); // Load without distance
            }
        }
    }

    private void loadLibraries() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                List<LibraryLocation> libraries = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    LibraryLocation lib = data.getValue(LibraryLocation.class);
                    if (lib != null) {
                        if (userLocation != null) {
                            float[] results = new float[1];
                            Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(), lib.getLatitude(), lib.getLongitude(), results);
                            lib.setDistance(results[0]);
                        }
                        libraries.add(lib);
                    }
                }
                
                if (userLocation != null) {
                    Collections.sort(libraries, new Comparator<LibraryLocation>() {
                        @Override
                        public int compare(LibraryLocation o1, LibraryLocation o2) {
                            return Float.compare(o1.getDistance(), o2.getDistance());
                        }
                    });
                }

                if (libraries.isEmpty()) {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
                    binding.rvLibraryLocations.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
                    binding.rvLibraryLocations.setVisibility(View.VISIBLE);
                    adapter.setLibraries(libraries);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(requireContext(), "Failed to load libraries.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
