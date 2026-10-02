package com.example.smartlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smartlibrary.models.User;

public class SessionManager {
    private static final String PREF_NAME = "SmartLibrarySession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_NAME = "userName";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_MOBILE = "userMobile";
    private static final String KEY_ROLL = "userRollNumber";
    private static final String KEY_ROLE = "userRole";
    private static final String KEY_PROFILE_IMAGE = "userProfileImage";
    private static final String KEY_DARK_MODE = "isDarkMode";
    private static final String KEY_NOTIFICATIONS = "isNotificationsEnabled";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUserSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getUserId() != null ? user.getUserId() : "user_101");
        editor.putString(KEY_NAME, user.getName() != null ? user.getName() : "Smart Student");
        editor.putString(KEY_EMAIL, user.getEmail() != null ? user.getEmail() : "student@smartlibrary.edu");
        editor.putString(KEY_MOBILE, user.getMobile() != null ? user.getMobile() : "");
        editor.putString(KEY_ROLL, user.getRollNumber() != null ? user.getRollNumber() : "");
        editor.putString(KEY_ROLE, user.getRole() != null ? user.getRole() : "student");
        editor.putString(KEY_PROFILE_IMAGE, user.getProfileImage() != null ? user.getProfileImage() : "");
        editor.apply();
    }

    public User getUserSession() {
        if (!isLoggedIn()) return null;
        User user = new User();
        user.setUserId(getStringSafe(KEY_USER_ID, "user_101"));
        user.setName(getStringSafe(KEY_NAME, "Smart Student"));
        user.setEmail(getStringSafe(KEY_EMAIL, "student@smartlibrary.edu"));
        user.setMobile(getStringSafe(KEY_MOBILE, "+1 555-0199"));
        user.setRollNumber(getStringSafe(KEY_ROLL, "SL-2026-889"));
        user.setRole(getStringSafe(KEY_ROLE, "student"));
        user.setProfileImage(getStringSafe(KEY_PROFILE_IMAGE, ""));
        return user;
    }

    public boolean isLoggedIn() {
        return getBooleanSafe(KEY_IS_LOGGED_IN, false);
    }

    public void setDarkMode(boolean isDark) {
        editor.putBoolean(KEY_DARK_MODE, isDark);
        editor.apply();
    }

    public boolean isDarkMode() {
        return getBooleanSafe(KEY_DARK_MODE, false);
    }

    public void setNotificationsEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public boolean isNotificationsEnabled() {
        return getBooleanSafe(KEY_NOTIFICATIONS, true);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    private String getStringSafe(String key, String defaultValue) {
        try {
            return pref.getString(key, defaultValue);
        } catch (ClassCastException e) {
            try {
                int intValue = pref.getInt(key, 0);
                return String.valueOf(intValue);
            } catch (Exception ex) {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean getBooleanSafe(String key, boolean defaultValue) {
        try {
            return pref.getBoolean(key, defaultValue);
        } catch (ClassCastException e) {
            try {
                String strVal = pref.getString(key, String.valueOf(defaultValue));
                return Boolean.parseBoolean(strVal);
            } catch (Exception ex) {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
