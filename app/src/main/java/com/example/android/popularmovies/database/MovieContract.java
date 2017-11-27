package com.example.android.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines supported URIs and columns for the tables of the movies database.
 */

public final class MovieContract {

    private MovieContract() {

    }

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVOURITES = "favourites";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();
        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_API_ID = "api_id";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_DESCRIPTION = "description";
    }


}
