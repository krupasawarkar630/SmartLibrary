package com.example.smartlibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.models.FeedbackItem;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.models.LearningPoint;
import com.example.smartlibrary.models.LibraryLocation;
import com.example.smartlibrary.models.NotificationItem;
import com.example.smartlibrary.models.QuizQuestion;
import com.example.smartlibrary.models.QuizResult;
import com.example.smartlibrary.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartLibrary.db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_BOOKS = "books";
    private static final String TABLE_REQUESTS = "requests";
    private static final String TABLE_ISSUED_BOOKS = "issued_books";
    private static final String TABLE_LEARNING_POINTS = "learning_points";
    private static final String TABLE_LIBRARIES = "libraries";
    private static final String TABLE_QUIZ_QUESTIONS = "quiz_questions";
    private static final String TABLE_QUIZ_RESULTS = "quiz_results";
    private static final String TABLE_FEEDBACK = "feedback";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "user_id TEXT PRIMARY KEY, " +
                "name TEXT, email TEXT UNIQUE, mobile TEXT, roll_number TEXT, password TEXT, profile_image TEXT, role TEXT, created_at INTEGER)");

        // Books Table
        db.execSQL("CREATE TABLE " + TABLE_BOOKS + " (" +
                "book_id TEXT PRIMARY KEY, title TEXT, author TEXT, category TEXT, isbn TEXT, publisher TEXT, pub_year INTEGER, " +
                "total_copies INTEGER, avail_copies INTEGER, description TEXT, cover_image TEXT, location_id TEXT, rating REAL)");

        // Requests Table
        db.execSQL("CREATE TABLE " + TABLE_REQUESTS + " (" +
                "request_id TEXT PRIMARY KEY, user_id TEXT, user_name TEXT, book_id TEXT, book_title TEXT, book_author TEXT, book_cover TEXT, " +
                "status TEXT, requested_at INTEGER, approved_at INTEGER, expires_at INTEGER, qr_token TEXT)");

        // Issued Books Table
        db.execSQL("CREATE TABLE " + TABLE_ISSUED_BOOKS + " (" +
                "issued_id TEXT PRIMARY KEY, user_id TEXT, book_id TEXT, request_id TEXT, book_title TEXT, book_author TEXT, book_cover TEXT, " +
                "issue_date INTEGER, due_date INTEGER, return_date INTEGER, status TEXT, fine REAL, learning_point TEXT)");

        // Learning Points Table
        db.execSQL("CREATE TABLE " + TABLE_LEARNING_POINTS + " (" +
                "id TEXT PRIMARY KEY, user_id TEXT, book_id TEXT, book_title TEXT, insight_text TEXT, rating REAL, timestamp INTEGER)");

        // Libraries Table
        db.execSQL("CREATE TABLE " + TABLE_LIBRARIES + " (" +
                "id TEXT PRIMARY KEY, name TEXT, address TEXT, hours TEXT, phone TEXT, latitude REAL, longitude REAL)");

        // Quiz Questions Table
        db.execSQL("CREATE TABLE " + TABLE_QUIZ_QUESTIONS + " (" +
                "id TEXT PRIMARY KEY, question TEXT, opt_a TEXT, opt_b TEXT, opt_c TEXT, opt_d TEXT, correct_idx INTEGER, explanation TEXT)");

        // Quiz Results Table
        db.execSQL("CREATE TABLE " + TABLE_QUIZ_RESULTS + " (" +
                "id TEXT PRIMARY KEY, user_id TEXT, user_name TEXT, score INTEGER, total_q INTEGER, percentage REAL, timestamp INTEGER)");

        // Feedback Table
        db.execSQL("CREATE TABLE " + TABLE_FEEDBACK + " (" +
                "id TEXT PRIMARY KEY, user_id TEXT, user_name TEXT, rating REAL, comments TEXT, timestamp INTEGER)");

        // Notifications Table
        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                "id TEXT PRIMARY KEY, user_id TEXT, title TEXT, message TEXT, timestamp INTEGER, is_read INTEGER, type TEXT)");

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ISSUED_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNING_POINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Seed Default Admin and Demo User
        ContentValues demoUser = new ContentValues();
        demoUser.put("user_id", "user_101");
        demoUser.put("name", "Shravani Dafe");
        demoUser.put("email", "shravani@smartlibrary.edu");
        demoUser.put("mobile", "+1 555-0199");
        demoUser.put("roll_number", "SL-2026-889");
        demoUser.put("password", "password123");
        demoUser.put("profile_image", "");
        demoUser.put("role", "student");
        demoUser.put("created_at", System.currentTimeMillis());
        db.insert(TABLE_USERS, null, demoUser);

        // Seed Sample Books
        insertSeedBook(db, "BK_001", "Think Like a Monk", "Jay Shetty", "Personal Development", "9781982134488", "Simon & Schuster", 2020, 5, 3, "Train your mind for peace and purpose every day.", "", "LIB_MAIN", 4.8f);
        insertSeedBook(db, "BK_002", "Ikigai", "Héctor García", "Philosophy & Religion", "9780143130727", "Penguin Books", 2016, 8, 5, "The Japanese secret to a long and happy life.", "", "LIB_MAIN", 4.7f);
        insertSeedBook(db, "BK_003", "The Power of Moments", "Chip Heath", "Business", "9781501147760", "Simon & Schuster", 2017, 4, 2, "Why certain experiences have extraordinary impact.", "", "LIB_SCIENCE", 4.6f);
        insertSeedBook(db, "BK_004", "The Road Less Traveled", "M. Scott Peck", "Psychology", "9780743243155", "Touchstone", 1978, 3, 1, "A new psychology of love, traditional values and spiritual growth.", "", "LIB_MAIN", 4.5f);
        insertSeedBook(db, "BK_005", "Emotional Blackmail", "Susan Forward", "Psychology", "9780060928972", "Harper Paperbacks", 1997, 2, 0, "When the people in your life use fear, obligation, and guilt to manipulate you.", "", "LIB_NORTH", 4.4f);
        insertSeedBook(db, "BK_006", "Presenting to Win", "Jerry Weissman", "Marketing", "9780130464132", "FT Press", 2008, 6, 4, "The art of telling your story to get results.", "", "LIB_SCIENCE", 4.6f);
        insertSeedBook(db, "BK_007", "The Art of Letting Go", "Nick Trenton", "Personal Development", "9781954182851", "PKCS Publishing", 2021, 5, 5, "Stop overthinking, find emotional peace, and master detachment.", "", "LIB_MAIN", 4.9f);
        insertSeedBook(db, "BK_008", "A Girl to Remember", "Ajay K. Pandey", "Fiction", "9789387022379", "Srishti Publishers", 2018, 7, 6, "An inspiring emotional romance about love, destiny and memory.", "", "LIB_MAIN", 4.7f);
        insertSeedBook(db, "BK_009", "Atomic Habits", "James Clear", "Personal Development", "9780735211292", "Avery", 2018, 10, 8, "An easy & proven way to build good habits & break bad ones.", "", "LIB_MAIN", 4.95f);
        insertSeedBook(db, "BK_010", "Deep Work", "Cal Newport", "Parenting & Education", "9781455586691", "Grand Central Publishing", 2016, 4, 2, "Rules for focused success in a distracted world.", "", "LIB_SCIENCE", 4.85f);

        // Seed Library Locations
        insertSeedLibrary(db, "LIB_MAIN", "Central Smart Library", "100 Academic Way, Campus Center", "Mon-Sat: 8:00 AM - 10:00 PM", "+1 (555) 234-5678", 37.7749, -122.4194);
        insertSeedLibrary(db, "LIB_SCIENCE", "Science & Innovation Reading Wing", "205 Research Blvd, Hall B", "Mon-Fri: 9:00 AM - 9:00 PM", "+1 (555) 345-6789", 37.7833, -122.4167);
        insertSeedLibrary(db, "LIB_NORTH", "North Quad Graduate Library", "45 North Avenue, Quad Building", "Mon-Sun: 7:00 AM - 11:00 PM", "+1 (555) 456-7890", 37.7695, -122.4467);

        // Seed Sample Approved Request with QR
        long now = System.currentTimeMillis();
        long expireTime = now + (24 * 60 * 60 * 1000);
        ContentValues approvedReq = new ContentValues();
        approvedReq.put("request_id", "REQ_1001");
        approvedReq.put("user_id", "user_101");
        approvedReq.put("user_name", "Shravani Dafe");
        approvedReq.put("book_id", "BK_001");
        approvedReq.put("book_title", "Think Like a Monk");
        approvedReq.put("book_author", "Jay Shetty");
        approvedReq.put("book_cover", "");
        approvedReq.put("status", "APPROVED");
        approvedReq.put("requested_at", now - (2 * 3600 * 1000));
        approvedReq.put("approved_at", now - (1 * 3600 * 1000));
        approvedReq.put("expires_at", expireTime);
        approvedReq.put("qr_token", "QR_TOKEN_REQ_1001_BK_001_USER_101");
        db.insert(TABLE_REQUESTS, null, approvedReq);

        // Seed Sample Issued Book
        long issueDate = now - (5 * 24 * 3600 * 1000);
        long dueDate = now + (9 * 24 * 3600 * 1000);
        ContentValues issuedBook = new ContentValues();
        issuedBook.put("issued_id", "ISS_5001");
        issuedBook.put("user_id", "user_101");
        issuedBook.put("book_id", "BK_002");
        issuedBook.put("request_id", "REQ_0999");
        issuedBook.put("book_title", "Ikigai");
        issuedBook.put("book_author", "Héctor García");
        issuedBook.put("book_cover", "");
        issuedBook.put("issue_date", issueDate);
        issuedBook.put("due_date", dueDate);
        issuedBook.put("return_date", 0);
        issuedBook.put("status", "ISSUED");
        issuedBook.put("fine", 0.0);
        issuedBook.put("learning_point", "Finding flow in daily tasks gives deep sense of joy.");
        db.insert(TABLE_ISSUED_BOOKS, null, issuedBook);

        // Seed Sample History Item
        long oldIssue = now - (30 * 24 * 3600 * 1000);
        long oldReturn = now - (16 * 24 * 3600 * 1000);
        ContentValues historyBook = new ContentValues();
        historyBook.put("issued_id", "ISS_4901");
        historyBook.put("user_id", "user_101");
        historyBook.put("book_id", "BK_009");
        historyBook.put("request_id", "REQ_0888");
        historyBook.put("book_title", "Atomic Habits");
        historyBook.put("book_author", "James Clear");
        historyBook.put("book_cover", "");
        historyBook.put("issue_date", oldIssue);
        historyBook.put("due_date", oldIssue + (14 * 24 * 3600 * 1000));
        historyBook.put("return_date", oldReturn);
        historyBook.put("status", "RETURNED");
        historyBook.put("fine", 0.0);
        historyBook.put("learning_point", "1% better every day leads to massive compounding results.");
        db.insert(TABLE_ISSUED_BOOKS, null, historyBook);

        // Seed Quiz Questions
        insertSeedQuiz(db, "Q_01", "What does the ISBN acronym stand for in library management systems?", "International Standard Book Number", "Integrated Serial Book System", "International Serial Bibliography Network", "Indexed Standard Bibliographic Number", 0, "ISBN stands for International Standard Book Number.");
        insertSeedQuiz(db, "Q_02", "Who wrote the classic self-development book 'Atomic Habits'?", "Cal Newport", "James Clear", "Jay Shetty", "Simon Sinek", 1, "Atomic Habits was authored by James Clear in 2018.");
        insertSeedQuiz(db, "Q_03", "Which QR Code scanning format protocol is commonly used for temporary security tokens?", "JSON / ZXing Matrix Payload", "Plaintext Password", "Unencrypted Raw String", "XML Data Blob", 0, "ZXing JSON formatted tokens allow server side verification with expiration timestamps.");
        insertSeedQuiz(db, "Q_04", "What Japanese term describes 'a reason for being' or finding life purpose?", "Kaizen", "Ikigai", "Wabi-Sabi", "Kintsugi", 1, "Ikigai refers to finding purpose at the intersection of passion, mission, vocation, and profession.");

        // Seed Notifications
        insertSeedNotification(db, "NOTIF_01", "user_101", "Book Request Approved!", "Your request for 'Think Like a Monk' has been approved. Show your QR code at the desk to collect it.", now - 3600000, 0, "REQUEST_APPROVED");
        insertSeedNotification(db, "NOTIF_02", "user_101", "Due Date Reminder", "'Ikigai' is due in 9 days. Please return or extend on time to avoid fines.", now - 86400000, 1, "DUE_REMINDER");
    }

    private void insertSeedBook(SQLiteDatabase db, String id, String title, String author, String cat, String isbn, String pub, int year, int total, int avail, String desc, String img, String loc, float rating) {
        ContentValues cv = new ContentValues();
        cv.put("book_id", id);
        cv.put("title", title);
        cv.put("author", author);
        cv.put("category", cat);
        cv.put("isbn", isbn);
        cv.put("publisher", pub);
        cv.put("pub_year", year);
        cv.put("total_copies", total);
        cv.put("avail_copies", avail);
        cv.put("description", desc);
        cv.put("cover_image", img);
        cv.put("location_id", loc);
        cv.put("rating", rating);
        db.insert(TABLE_BOOKS, null, cv);
    }

    private void insertSeedLibrary(SQLiteDatabase db, String id, String name, String addr, String hours, String phone, double lat, double lng) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("address", addr);
        cv.put("hours", hours);
        cv.put("phone", phone);
        cv.put("latitude", lat);
        cv.put("longitude", lng);
        db.insert(TABLE_LIBRARIES, null, cv);
    }

    private void insertSeedQuiz(SQLiteDatabase db, String id, String q, String optA, String optB, String optC, String optD, int correctIdx, String exp) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("question", q);
        cv.put("opt_a", optA);
        cv.put("opt_b", optB);
        cv.put("opt_c", optC);
        cv.put("opt_d", optD);
        cv.put("correct_idx", correctIdx);
        cv.put("explanation", exp);
        db.insert(TABLE_QUIZ_QUESTIONS, null, cv);
    }

    private void insertSeedNotification(SQLiteDatabase db, String id, String userId, String title, String msg, long ts, int read, String type) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("user_id", userId);
        cv.put("title", title);
        cv.put("message", msg);
        cv.put("timestamp", ts);
        cv.put("is_read", read);
        cv.put("type", type);
        db.insert(TABLE_NOTIFICATIONS, null, cv);
    }

    // --- USER CRUD ---
    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", user.getUserId() != null ? user.getUserId() : UUID.randomUUID().toString());
        cv.put("name", user.getName());
        cv.put("email", user.getEmail());
        cv.put("mobile", user.getMobile());
        cv.put("roll_number", user.getRollNumber());
        cv.put("password", user.getPassword());
        cv.put("profile_image", user.getProfileImage());
        cv.put("role", user.getRole() != null ? user.getRole() : "student");
        cv.put("created_at", System.currentTimeMillis());
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?", new String[]{email, password});
        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean updateUserProfile(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", user.getName());
        cv.put("mobile", user.getMobile());
        cv.put("roll_number", user.getRollNumber());
        cv.put("profile_image", user.getProfileImage());
        int rows = db.update(TABLE_USERS, cv, "user_id=?", new String[]{user.getUserId()});
        return rows > 0;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int rows = db.update(TABLE_USERS, cv, "email=?", new String[]{email});
        return rows > 0;
    }

    private User cursorToUser(Cursor cursor) {
        return new User(
                cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("mobile")),
                cursor.getString(cursor.getColumnIndexOrThrow("roll_number")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("profile_image")),
                cursor.getString(cursor.getColumnIndexOrThrow("role")),
                cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
        );
    }

    // --- BOOKS CRUD ---
    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToBook(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Book getBookById(String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS + " WHERE book_id=?", new String[]{bookId});
        if (cursor != null && cursor.moveToFirst()) {
            Book book = cursorToBook(cursor);
            cursor.close();
            return book;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public Book findBookByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        String trimmed = code.trim();
        Book book = getBookById(trimmed);
        if (book != null) return book;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS + " WHERE isbn=? OR title LIKE ?", new String[]{trimmed, "%" + trimmed + "%"});
        if (cursor != null && cursor.moveToFirst()) {
            book = cursorToBook(cursor);
            cursor.close();
            return book;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public List<Book> searchBooks(String query, String category) {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder sql = new StringBuilder("SELECT * FROM " + TABLE_BOOKS + " WHERE 1=1");
        List<String> args = new ArrayList<>();
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
            String q = "%" + query.trim() + "%";
            args.add(q); args.add(q); args.add(q);
        }
        if (category != null && !category.equalsIgnoreCase("All")) {
            sql.append(" AND category=?");
            args.add(category);
        }
        Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToBook(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    private Book cursorToBook(Cursor cursor) {
        return new Book(
                cursor.getString(cursor.getColumnIndexOrThrow("book_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("author")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("isbn")),
                cursor.getString(cursor.getColumnIndexOrThrow("publisher")),
                cursor.getInt(cursor.getColumnIndexOrThrow("pub_year")),
                cursor.getInt(cursor.getColumnIndexOrThrow("total_copies")),
                cursor.getInt(cursor.getColumnIndexOrThrow("avail_copies")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("cover_image")),
                cursor.getString(cursor.getColumnIndexOrThrow("location_id")),
                cursor.getFloat(cursor.getColumnIndexOrThrow("rating"))
        );
    }

    // --- REQUESTS CRUD ---
    public boolean createBookRequest(BookRequest req) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if user already has pending or approved request for this book
        Cursor check = db.rawQuery("SELECT * FROM " + TABLE_REQUESTS + " WHERE user_id=? AND book_id=? AND (status='PENDING' OR status='APPROVED')",
                new String[]{req.getUserId(), req.getBookId()});
        if (check != null && check.getCount() > 0) {
            check.close();
            return false;
        }
        if (check != null) check.close();

        ContentValues cv = new ContentValues();
        cv.put("request_id", req.getRequestId() != null ? req.getRequestId() : "REQ_" + System.currentTimeMillis());
        cv.put("user_id", req.getUserId());
        cv.put("user_name", req.getUserName());
        cv.put("book_id", req.getBookId());
        cv.put("book_title", req.getBookTitle());
        cv.put("book_author", req.getBookAuthor());
        cv.put("book_cover", req.getBookCoverImage());
        cv.put("status", req.getStatus() != null ? req.getStatus() : "PENDING");
        cv.put("requested_at", req.getRequestedAt() > 0 ? req.getRequestedAt() : System.currentTimeMillis());
        cv.put("approved_at", req.getApprovedAt());
        cv.put("expires_at", req.getExpiresAt());
        cv.put("qr_token", req.getQrToken());
        long result = db.insert(TABLE_REQUESTS, null, cv);
        return result != -1;
    }

    public List<BookRequest> getUserRequests(String userId) {
        List<BookRequest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REQUESTS + " WHERE user_id=? ORDER BY requested_at DESC", new String[]{userId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToRequest(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public BookRequest getRequestById(String requestId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REQUESTS + " WHERE request_id=?", new String[]{requestId});
        if (cursor != null && cursor.moveToFirst()) {
            BookRequest req = cursorToRequest(cursor);
            cursor.close();
            return req;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean updateRequestStatus(String requestId, String status, String qrToken, long approvedAt, long expiresAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        if (qrToken != null) cv.put("qr_token", qrToken);
        if (approvedAt > 0) cv.put("approved_at", approvedAt);
        if (expiresAt > 0) cv.put("expires_at", expiresAt);
        int rows = db.update(TABLE_REQUESTS, cv, "request_id=?", new String[]{requestId});
        return rows > 0;
    }

    private BookRequest cursorToRequest(Cursor cursor) {
        return new BookRequest(
                cursor.getString(cursor.getColumnIndexOrThrow("request_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("user_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_title")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_author")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_cover")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getLong(cursor.getColumnIndexOrThrow("requested_at")),
                cursor.getLong(cursor.getColumnIndexOrThrow("approved_at")),
                cursor.getLong(cursor.getColumnIndexOrThrow("expires_at")),
                cursor.getString(cursor.getColumnIndexOrThrow("qr_token"))
        );
    }

    // --- ISSUED BOOKS CRUD ---
    public boolean issueBookToUser(IssuedBook book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("issued_id", book.getIssuedBookId() != null ? book.getIssuedBookId() : "ISS_" + System.currentTimeMillis());
        cv.put("user_id", book.getUserId());
        cv.put("book_id", book.getBookId());
        cv.put("request_id", book.getRequestId());
        cv.put("book_title", book.getBookTitle());
        cv.put("book_author", book.getBookAuthor());
        cv.put("book_cover", book.getBookCoverImage());
        cv.put("issue_date", book.getIssueDate() > 0 ? book.getIssueDate() : System.currentTimeMillis());
        cv.put("due_date", book.getDueDate() > 0 ? book.getDueDate() : System.currentTimeMillis() + (14L * 24 * 3600 * 1000));
        cv.put("return_date", 0);
        cv.put("status", "ISSUED");
        cv.put("fine", 0.0);
        cv.put("learning_point", "");
        long result = db.insert(TABLE_ISSUED_BOOKS, null, cv);

        // Decrement available copies
        db.execSQL("UPDATE " + TABLE_BOOKS + " SET avail_copies = MAX(0, avail_copies - 1) WHERE book_id=?", new String[]{book.getBookId()});
        return result != -1;
    }

    public List<IssuedBook> getActiveIssuedBooks(String userId) {
        List<IssuedBook> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ISSUED_BOOKS + " WHERE user_id=? AND status='ISSUED' ORDER BY issue_date DESC", new String[]{userId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToIssuedBook(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<IssuedBook> getIssuedBookHistory(String userId) {
        List<IssuedBook> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ISSUED_BOOKS + " WHERE user_id=? ORDER BY issue_date DESC", new String[]{userId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToIssuedBook(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public boolean returnIssuedBook(String issuedId, String learningPoint) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ISSUED_BOOKS + " WHERE issued_id=?", new String[]{issuedId});
        if (cursor != null && cursor.moveToFirst()) {
            String bookId = cursor.getString(cursor.getColumnIndexOrThrow("book_id"));
            cursor.close();

            ContentValues cv = new ContentValues();
            cv.put("status", "RETURNED");
            cv.put("return_date", System.currentTimeMillis());
            if (learningPoint != null && !learningPoint.trim().isEmpty()) {
                cv.put("learning_point", learningPoint);
            }
            db.update(TABLE_ISSUED_BOOKS, cv, "issued_id=?", new String[]{issuedId});
            db.execSQL("UPDATE " + TABLE_BOOKS + " SET avail_copies = avail_copies + 1 WHERE book_id=?", new String[]{bookId});
            return true;
        }
        if (cursor != null) cursor.close();
        return false;
    }

    private IssuedBook cursorToIssuedBook(Cursor cursor) {
        return new IssuedBook(
                cursor.getString(cursor.getColumnIndexOrThrow("issued_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("request_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_title")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_author")),
                cursor.getString(cursor.getColumnIndexOrThrow("book_cover")),
                cursor.getLong(cursor.getColumnIndexOrThrow("issue_date")),
                cursor.getLong(cursor.getColumnIndexOrThrow("due_date")),
                cursor.getLong(cursor.getColumnIndexOrThrow("return_date")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("fine")),
                cursor.getString(cursor.getColumnIndexOrThrow("learning_point"))
        );
    }

    // --- LEARNING POINTS ---
    public boolean addLearningPoint(LearningPoint lp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", lp.getId() != null ? lp.getId() : UUID.randomUUID().toString());
        cv.put("user_id", lp.getUserId());
        cv.put("book_id", lp.getBookId());
        cv.put("book_title", lp.getBookTitle());
        cv.put("insight_text", lp.getInsightText());
        cv.put("rating", lp.getRating());
        cv.put("timestamp", lp.getTimestamp() > 0 ? lp.getTimestamp() : System.currentTimeMillis());
        long result = db.insert(TABLE_LEARNING_POINTS, null, cv);
        return result != -1;
    }

    public List<LearningPoint> getUserLearningPoints(String userId) {
        List<LearningPoint> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LEARNING_POINTS + " WHERE user_id=? ORDER BY timestamp DESC", new String[]{userId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new LearningPoint(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("book_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("book_title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("insight_text")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // --- LIBRARIES ---
    public List<LibraryLocation> getAllLibraries() {
        List<LibraryLocation> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LIBRARIES, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new LibraryLocation(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("address")),
                        cursor.getString(cursor.getColumnIndexOrThrow("hours")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // --- QUIZ ---
    public List<QuizQuestion> getAllQuizQuestions() {
        List<QuizQuestion> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUIZ_QUESTIONS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new QuizQuestion(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("question")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opt_a")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opt_b")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opt_c")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opt_d")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("correct_idx")),
                        cursor.getString(cursor.getColumnIndexOrThrow("explanation"))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public boolean saveQuizResult(QuizResult qr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", qr.getId() != null ? qr.getId() : UUID.randomUUID().toString());
        cv.put("user_id", qr.getUserId());
        cv.put("user_name", qr.getUserName());
        cv.put("score", qr.getScore());
        cv.put("total_q", qr.getTotalQuestions());
        cv.put("percentage", qr.getPercentage());
        cv.put("timestamp", qr.getTimestamp() > 0 ? qr.getTimestamp() : System.currentTimeMillis());
        long result = db.insert(TABLE_QUIZ_RESULTS, null, cv);
        return result != -1;
    }

    // --- FEEDBACK ---
    public boolean saveFeedback(FeedbackItem fb) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", fb.getId() != null ? fb.getId() : UUID.randomUUID().toString());
        cv.put("user_id", fb.getUserId());
        cv.put("user_name", fb.getUserName());
        cv.put("rating", fb.getRating());
        cv.put("comments", fb.getComments());
        cv.put("timestamp", fb.getTimestamp() > 0 ? fb.getTimestamp() : System.currentTimeMillis());
        long result = db.insert(TABLE_FEEDBACK, null, cv);
        return result != -1;
    }

    // --- NOTIFICATIONS ---
    public List<NotificationItem> getUserNotifications(String userId) {
        List<NotificationItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE user_id=? ORDER BY timestamp DESC", new String[]{userId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new NotificationItem(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("message")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_read")) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow("type"))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
