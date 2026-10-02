package com.example.smartlibrary.models;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private String publisher;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;
    private String description;
    private String coverImage;
    private String libraryId;
    private float rating;
    
    // Smart Book Location fields
    private String floor;
    private String room;
    private String section;
    private String shelf;
    private String shelfNumber;
    
    // Additional requested fields
    private String imageUrl;
    private String qrCode;
    private long createdAt;
    private long updatedAt;

    public Book() {
    }

    public Book(String bookId, String title, String author, String category, String isbn,
                String publisher, int publicationYear, int totalCopies, int availableCopies,
                String description, String coverImage, String libraryId, float rating) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.description = description;
        this.coverImage = coverImage;
        this.libraryId = libraryId;
        this.rating = rating;
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getLibraryId() { return libraryId; }
    public void setLibraryId(String libraryId) { this.libraryId = libraryId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getShelf() { return shelf; }
    public void setShelf(String shelf) { this.shelf = shelf; }

    public String getShelfNumber() { return shelfNumber; }
    public void setShelfNumber(String shelfNumber) { this.shelfNumber = shelfNumber; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    public String getEffectiveCoverImage() {
        if (coverImage != null && !coverImage.trim().isEmpty()) {
            return coverImage;
        }
        return imageUrl;
    }
}
