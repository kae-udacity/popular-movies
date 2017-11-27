package com.example.android.popularmovies.data;

/**
 * Stores the data of the trailer.
 */

public class Trailer {

    private String name;
    private String key;

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
