package com.example.smartlibrary;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class SmartLibraryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
