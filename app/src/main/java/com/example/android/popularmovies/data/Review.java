package com.example.android.popularmovies.data;

/**
 * Stores the data of the review.
 */

public class Review {

    private String author;
    private String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
