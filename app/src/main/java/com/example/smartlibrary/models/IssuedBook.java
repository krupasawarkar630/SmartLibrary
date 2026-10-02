package com.example.smartlibrary.models;

public class IssuedBook {
    private String issuedBookId;
    private String userId;
    private String bookId;
    private String requestId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverImage;
    private long issueDate;
    private long dueDate;
    private long returnDate;
    private String status; // ISSUED, RETURNED, OVERDUE
    private double fine;
    private String learningPoint;

    public IssuedBook() {
    }

    public IssuedBook(String issuedBookId, String userId, String bookId, String requestId,
                      String bookTitle, String bookAuthor, String bookCoverImage,
                      long issueDate, long dueDate, long returnDate, String status, double fine, String learningPoint) {
        this.issuedBookId = issuedBookId;
        this.userId = userId;
        this.bookId = bookId;
        this.requestId = requestId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookCoverImage = bookCoverImage;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
        this.learningPoint = learningPoint;
    }

    public String getIssuedBookId() { return issuedBookId; }
    public void setIssuedBookId(String issuedBookId) { this.issuedBookId = issuedBookId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public String getBookCoverImage() { return bookCoverImage; }
    public void setBookCoverImage(String bookCoverImage) { this.bookCoverImage = bookCoverImage; }

    public long getIssueDate() { return issueDate; }
    public void setIssueDate(long issueDate) { this.issueDate = issueDate; }

    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }

    public long getReturnDate() { return returnDate; }
    public void setReturnDate(long returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public String getLearningPoint() { return learningPoint; }
    public void setLearningPoint(String learningPoint) { this.learningPoint = learningPoint; }
}
