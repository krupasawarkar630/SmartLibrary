package com.example.smartlibrary.database;

import android.content.Context;
import android.util.Log;

import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseHelper sqliteHelper;

    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public interface ActionCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    private FirebaseManager(Context context) {
        sqliteHelper = DatabaseHelper.getInstance(context.getApplicationContext());
        try {
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
        } catch (Exception e) {
            Log.e(TAG, "Firebase Auth/DB not available: " + e.getMessage());
        }
    }

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                            String uid = mAuth.getCurrentUser().getUid();
                            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        User user = snapshot.getValue(User.class);
                                        if (user != null) {
                                            user.setLastLogin(System.currentTimeMillis());
                                            mDatabase.child("users").child(uid).child("lastLogin").setValue(user.getLastLogin());
                                            sqliteHelper.registerUser(user); // local fallback
                                            callback.onSuccess(user);
                                        } else {
                                            callback.onFailure("Failed to parse user data.");
                                        }
                                    } else {
                                        callback.onFailure("User record not found in database.");
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    callback.onFailure("Database error: " + error.getMessage());
                                }
                            });
                        } else {
                            callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Invalid email or password");
                        }
                    });
        } else {
            callback.onFailure("Firebase Auth not initialized.");
        }
    }

    public void registerUser(User user, AuthCallback callback) {
        if (mAuth != null) {
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                            String uid = mAuth.getCurrentUser().getUid();
                            user.setUserId(uid);
                            user.setProvider("email");
                            user.setCreatedAt(System.currentTimeMillis());
                            user.setLastLogin(System.currentTimeMillis());
                            
                            mDatabase.child("users").child(uid).setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    if(dbTask.isSuccessful()) {
                                        sqliteHelper.registerUser(user); // local fallback
                                        callback.onSuccess(user);
                                    } else {
                                        callback.onFailure("Failed to save user data.");
                                    }
                                });
                        } else {
                            callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Registration failed");
                        }
                    });
        } else {
            callback.onFailure("Firebase Auth not initialized.");
        }
    }

    public void sendPasswordResetEmail(String email, ActionCallback callback) {
        if (mAuth != null) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String err = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                            callback.onFailure(err);
                        }
                    });
        } else {
            User user = sqliteHelper.getUserByEmail(email);
            if (user != null) {
                callback.onSuccess();
            } else {
                callback.onFailure("User email not found");
            }
        }
    }

    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
        }
    }

    public void seedFirebaseData() {
        mDatabase.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Force update books to apply new OpenLibrary images
                seedBooks();
                
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    seedLibraries();
                    seedQuizzes();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void seedBooks() {
        Book[] books = {
            new Book("BK_001", "Think Like a Monk", "Jay Shetty", "Personal Development", "9781982134488", "Simon & Schuster", 2020, 5, 3, "Train your mind for peace and purpose every day.", "https://images.unsplash.com/photo-1544947950-fa07a98d237f?q=80&w=1000&auto=format&fit=crop", "LIB_MAIN", 4.8f),
            new Book("BK_002", "Ikigai", "Héctor García", "Philosophy & Religion", "9780143130727", "Penguin Books", 2016, 8, 5, "The Japanese secret to a long and happy life.", "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?q=80&w=1000&auto=format&fit=crop", "LIB_MAIN", 4.7f),
            new Book("BK_003", "The Power of Moments", "Chip Heath", "Business", "9781501147760", "Simon & Schuster", 2017, 4, 2, "Why certain experiences have extraordinary impact.", "https://images.unsplash.com/photo-1512820790803-83ca734da794?q=80&w=1000&auto=format&fit=crop", "LIB_SCIENCE", 4.6f),
            new Book("BK_004", "The Road Less Traveled", "M. Scott Peck", "Psychology", "9780743243155", "Touchstone", 1978, 3, 1, "A new psychology of love, traditional values and spiritual growth.", "https://images.unsplash.com/photo-1476275466078-4007374efac4?q=80&w=1000&auto=format&fit=crop", "LIB_MAIN", 4.5f),
            new Book("BK_005", "Emotional Blackmail", "Susan Forward", "Psychology", "9780060928972", "Harper Paperbacks", 1997, 2, 0, "When the people in your life use fear, obligation, and guilt to manipulate you.", "https://images.unsplash.com/photo-1511556820780-d912e42b4980?q=80&w=1000&auto=format&fit=crop", "LIB_NORTH", 4.4f),
            new Book("BK_006", "Presenting to Win", "Jerry Weissman", "Marketing", "9780130464132", "FT Press", 2008, 6, 4, "The art of telling your story to get results.", "https://images.unsplash.com/photo-1553729459-efe14ef6055d?q=80&w=1000&auto=format&fit=crop", "LIB_SCIENCE", 4.6f),
            new Book("BK_007", "The Art of Letting Go", "Nick Trenton", "Personal Development", "9781954182851", "PKCS Publishing", 2021, 5, 5, "Stop overthinking, find emotional peace, and master detachment.", "https://images.unsplash.com/photo-1499750310107-5fef28a66643?q=80&w=1000&auto=format&fit=crop", "LIB_MAIN", 4.9f),
            new Book("BK_008", "A Girl to Remember", "Ajay K. Pandey", "Fiction", "9789387022379", "Srishti Publishers", 2018, 7, 6, "An inspiring emotional romance about love, destiny and memory.", "https://images.unsplash.com/photo-1474932430478-367d16b99031?q=80&w=1000&auto=format&fit=crop", "LIB_MAIN", 4.7f),
            new Book("BK_009", "Atomic Habits", "James Clear", "Personal Development", "9780735211292", "Avery", 2018, 10, 8, "An easy & proven way to build good habits & break bad ones.", "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?q=80&w=1000&auto=format&fit=crop", "LIB_MAIN", 4.95f),
            new Book("BK_010", "Deep Work", "Cal Newport", "Parenting & Education", "9781455586691", "Grand Central Publishing", 2016, 4, 2, "Rules for focused success in a distracted world.", "https://images.unsplash.com/photo-1522881451255-f59ad836fdf8?q=80&w=1000&auto=format&fit=crop", "LIB_SCIENCE", 4.85f)
        };
        
        books[0].setFloor("1st Floor"); books[0].setRoom("Self-Help Room"); books[0].setSection("A"); books[0].setShelf("SHELF_101"); books[0].setShelfNumber("01");
        books[1].setFloor("1st Floor"); books[1].setRoom("Self-Help Room"); books[1].setSection("A"); books[1].setShelf("SHELF_101"); books[1].setShelfNumber("02");
        books[2].setFloor("2nd Floor"); books[2].setRoom("Business Wing"); books[2].setSection("C"); books[2].setShelf("SHELF_205"); books[2].setShelfNumber("12");
        books[3].setFloor("3rd Floor"); books[3].setRoom("Psychology Room"); books[3].setSection("B"); books[3].setShelf("SHELF_302"); books[3].setShelfNumber("05");
        
        for (Book b : books) mDatabase.child("books").child(b.getBookId()).setValue(b);
    }

    private void seedLibraries() {
        Map<String, Object> l1 = new HashMap<>(); l1.put("id", "LIB_MAIN"); l1.put("name", "Central Smart Library"); l1.put("address", "100 Academic Way, Campus Center"); l1.put("hours", "Mon-Sat: 8:00 AM - 10:00 PM"); l1.put("phone", "+1 (555) 234-5678"); l1.put("latitude", 37.7749); l1.put("longitude", -122.4194);
        Map<String, Object> l2 = new HashMap<>(); l2.put("id", "LIB_SCIENCE"); l2.put("name", "Science & Innovation Reading Wing"); l2.put("address", "205 Research Blvd, Hall B"); l2.put("hours", "Mon-Fri: 9:00 AM - 9:00 PM"); l2.put("phone", "+1 (555) 345-6789"); l2.put("latitude", 37.7833); l2.put("longitude", -122.4167);
        Map<String, Object> l3 = new HashMap<>(); l3.put("id", "LIB_NORTH"); l3.put("name", "North Quad Graduate Library"); l3.put("address", "45 North Avenue, Quad Building"); l3.put("hours", "Mon-Sun: 7:00 AM - 11:00 PM"); l3.put("phone", "+1 (555) 456-7890"); l3.put("latitude", 37.7695); l3.put("longitude", -122.4467);
        mDatabase.child("libraries").child("LIB_MAIN").setValue(l1);
        mDatabase.child("libraries").child("LIB_SCIENCE").setValue(l2);
        mDatabase.child("libraries").child("LIB_NORTH").setValue(l3);
    }

    private void seedQuizzes() {
        com.example.smartlibrary.models.QuizQuestion[] qs = {
            new com.example.smartlibrary.models.QuizQuestion("Q_01", "What does the ISBN acronym stand for in library management systems?", "International Standard Book Number", "Integrated Serial Book System", "International Serial Bibliography Network", "Indexed Standard Bibliographic Number", 0, "ISBN stands for International Standard Book Number."),
            new com.example.smartlibrary.models.QuizQuestion("Q_02", "Who wrote the classic self-development book 'Atomic Habits'?", "Cal Newport", "James Clear", "Jay Shetty", "Simon Sinek", 1, "Atomic Habits was authored by James Clear in 2018."),
            new com.example.smartlibrary.models.QuizQuestion("Q_03", "Which QR Code scanning format protocol is commonly used for temporary security tokens?", "JSON / ZXing Matrix Payload", "Plaintext Password", "Unencrypted Raw String", "XML Data Blob", 0, "ZXing JSON formatted tokens allow server side verification with expiration timestamps."),
            new com.example.smartlibrary.models.QuizQuestion("Q_04", "What Japanese term describes 'a reason for being' or finding life purpose?", "Kaizen", "Ikigai", "Wabi-Sabi", "Kintsugi", 1, "Ikigai refers to finding purpose at the intersection of passion, mission, vocation, and profession.")
        };
        for (com.example.smartlibrary.models.QuizQuestion q : qs) mDatabase.child("quiz").child(q.getId()).setValue(q);
    }
}
