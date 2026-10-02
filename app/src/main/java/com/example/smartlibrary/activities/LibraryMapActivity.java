package com.example.smartlibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartlibrary.databinding.ActivityLibraryMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.smartlibrary.models.LibraryLocation;


public class LibraryMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_LIBRARY_NAME = "extra_library_name";
    public static final String EXTRA_LIBRARY_ADDRESS = "extra_library_address";
    public static final String EXTRA_LIBRARY_HOURS = "extra_library_hours";
    public static final String EXTRA_LIBRARY_LAT = "extra_library_lat";
    public static final String EXTRA_LIBRARY_LNG = "extra_library_lng";

    private ActivityLibraryMapBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private DatabaseReference librariesRef;
    
    private java.util.Map<String, com.google.android.gms.maps.model.Marker> libraryMarkers = new java.util.HashMap<>();
    private LatLng firstLibraryLatLng = null;

    private String name = "Central Smart Library";
    private String address = "100 Academic Way, Campus Center";
    private String hours = "Mon-Sat: 8:00 AM - 10:00 PM";
    private double latitude = 18.5204; // Default to Pune, India
    private double longitude = 73.8567;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        librariesRef = FirebaseDatabase.getInstance().getReference("libraries");

        binding.btnBackMap.setOnClickListener(v -> finish());

        if (getIntent().hasExtra(EXTRA_LIBRARY_NAME)) {
            name = getIntent().getStringExtra(EXTRA_LIBRARY_NAME);
            address = getIntent().getStringExtra(EXTRA_LIBRARY_ADDRESS);
            hours = getIntent().getStringExtra(EXTRA_LIBRARY_HOURS);
            latitude = getIntent().getDoubleExtra(EXTRA_LIBRARY_LAT, 37.7749);
            longitude = getIntent().getDoubleExtra(EXTRA_LIBRARY_LNG, -122.4194);
        }

        binding.tvMapBranchName.setText("Nearby Libraries");
        binding.tvMapBranchAddress.setText("Explore libraries in your city");
        binding.tvMapBranchHours.setText("Check details by tapping markers");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        binding.btnNavigateDirections.setOnClickListener(v -> openExternalMapsDirections());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            addCityLibraries(googleMap);
            enableUserLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        mMap.setOnInfoWindowClickListener(marker -> {
            LibraryLocation lib = (LibraryLocation) marker.getTag();
            if (lib != null) {
                Intent intent = new Intent(LibraryMapActivity.this, LibraryDetailsActivity.class);
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_ID, lib.getId());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_NAME, lib.getName());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_ADDRESS, lib.getAddress());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_HOURS, lib.getOpeningHours());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_PHONE, lib.getPhone());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_LAT, lib.getLatitude());
                intent.putExtra(LibraryDetailsActivity.EXTRA_LIBRARY_LNG, lib.getLongitude());
                startActivity(intent);
            }
        });
    }

    private void addCityLibraries(GoogleMap map) {
        librariesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                LibraryLocation lib = snapshot.getValue(LibraryLocation.class);
                if (lib != null) {
                    LatLng loc = new LatLng(lib.getLatitude(), lib.getLongitude());
                    if (firstLibraryLatLng == null) {
                        firstLibraryLatLng = loc;
                    }
                    com.google.android.gms.maps.model.Marker marker = map.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(lib.getName())
                            .snippet(lib.getAddress()));
                    if (marker != null) {
                        marker.setTag(lib);
                        libraryMarkers.put(snapshot.getKey(), marker);
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                LibraryLocation lib = snapshot.getValue(LibraryLocation.class);
                if (lib != null && libraryMarkers.containsKey(snapshot.getKey())) {
                    com.google.android.gms.maps.model.Marker marker = libraryMarkers.get(snapshot.getKey());
                    if (marker != null) {
                        marker.setPosition(new LatLng(lib.getLatitude(), lib.getLongitude()));
                        marker.setTitle(lib.getName());
                        marker.setSnippet(lib.getAddress());
                        marker.setTag(lib);
                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (libraryMarkers.containsKey(snapshot.getKey())) {
                    com.google.android.gms.maps.model.Marker marker = libraryMarkers.get(snapshot.getKey());
                    if (marker != null) {
                        marker.remove();
                    }
                    libraryMarkers.remove(snapshot.getKey());
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LibraryMapActivity.this, "Failed to load libraries.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));
                } else {
                    // Fallback to first library
                    if (firstLibraryLatLng != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLibraryLatLng, 12f));
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Showing libraries.", Toast.LENGTH_LONG).show();
                if (firstLibraryLatLng != null && mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLibraryLatLng, 12f));
                }
            }
        }
    }

    private void openExternalMapsDirections() {
        try {
            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(name + ", " + address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Intent genericIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude));
                startActivity(genericIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not open map navigation.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}
