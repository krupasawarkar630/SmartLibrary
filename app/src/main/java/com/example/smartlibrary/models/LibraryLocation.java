package com.example.smartlibrary.models;

public class LibraryLocation {
    private String id;
    private String name;
    private String address;
    private String openingHours;
    private String phone;
    private double latitude;
    private double longitude;
    
    @com.google.firebase.database.Exclude
    private float distance;

    public LibraryLocation() {
    }

    public LibraryLocation(String id, String name, String address, String openingHours, String phone, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @com.google.firebase.database.Exclude
    public float getDistance() { return distance; }
    
    @com.google.firebase.database.Exclude
    public void setDistance(float distance) { this.distance = distance; }
}
