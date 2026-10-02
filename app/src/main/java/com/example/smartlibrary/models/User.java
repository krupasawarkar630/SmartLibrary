package com.example.smartlibrary.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String mobile;
    private String rollNumber;
    private String password;
    private String profileImage;
    private String role; // "student" or "admin"
    private String provider;
    private long lastLogin;
    private long createdAt;

    public User() {
    }

    public User(String userId, String name, String email, String mobile, String rollNumber, String password, String profileImage, String role, long createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.rollNumber = rollNumber;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }
}
