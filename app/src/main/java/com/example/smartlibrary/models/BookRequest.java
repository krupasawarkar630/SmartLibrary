package com.example.smartlibrary.models;

public class BookRequest {
    private String requestId;
    private String userId;
    private String userName;
    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverImage;
    private String status; // PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED
    private long requestedAt;
    private long approvedAt;
    private long expiresAt;
    private String qrToken;

    public BookRequest() {
    }

    public BookRequest(String requestId, String userId, String userName, String bookId,
                       String bookTitle, String bookAuthor, String bookCoverImage,
                       String status, long requestedAt, long approvedAt, long expiresAt, String qrToken) {
        this.requestId = requestId;
        this.userId = userId;
        this.userName = userName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookCoverImage = bookCoverImage;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.expiresAt = expiresAt;
        this.qrToken = qrToken;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public String getBookCoverImage() { return bookCoverImage; }
    public void setBookCoverImage(String bookCoverImage) { this.bookCoverImage = bookCoverImage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getRequestedAt() { return requestedAt; }
    public void setRequestedAt(long requestedAt) { this.requestedAt = requestedAt; }

    public long getApprovedAt() { return approvedAt; }
    public void setApprovedAt(long approvedAt) { this.approvedAt = approvedAt; }

    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }

    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }
}
