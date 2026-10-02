package com.example.smartlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

public class QRCodeUtils {

    public static Bitmap generateQRCode(String text, int width, int height) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
            int matrixWidth = bitMatrix.getWidth();
            int matrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[matrixWidth * matrixHeight];
            for (int y = 0; y < matrixHeight; y++) {
                int offset = y * matrixWidth;
                for (int x = 0; x < matrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createSecureTokenPayload(String requestId, String bookId, String userId, long expiresAt) {
        try {
            JSONObject json = new JSONObject();
            json.put("requestId", requestId);
            json.put("bookId", bookId);
            json.put("userId", userId);
            json.put("expiresAt", expiresAt);
            json.put("temporaryToken", "TOKEN_" + System.currentTimeMillis() + "_" + requestId.hashCode());
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return requestId + ":" + bookId + ":" + userId;
        }
    }
}
