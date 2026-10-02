package com.example.smartlibrary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

    public static String formatDate(long millis) {
        if (millis <= 0) return "N/A";
        return DATE_FORMATTER.format(new Date(millis));
    }

    public static String formatDateTime(long millis) {
        if (millis <= 0) return "N/A";
        return DATE_TIME_FORMATTER.format(new Date(millis));
    }

    public static String formatTime(long millis) {
        if (millis <= 0) return "N/A";
        return TIME_FORMATTER.format(new Date(millis));
    }

    public static long getDaysRemaining(long dueDateMillis) {
        long diff = dueDateMillis - System.currentTimeMillis();
        if (diff <= 0) return 0;
        return diff / (1000 * 60 * 60 * 24);
    }

    public static double calculateFine(long dueDateMillis) {
        if (System.currentTimeMillis() <= dueDateMillis) return 0.0;
        long diff = System.currentTimeMillis() - dueDateMillis;
        long daysOverdue = (diff / (1000 * 60 * 60 * 24)) + 1;
        return daysOverdue * 1.50; // $1.50 fine per overdue day
    }
}
