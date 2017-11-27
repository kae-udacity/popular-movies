package com.example.android.popularmovies.util;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.database.MovieContract.MovieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides helper methods for retrieving data from a {@link Cursor} and storing it in
 * a {@link Movie} object.
 */

public final class DatabaseUtils {

    private DatabaseUtils() {

    }

    public static List<Movie> extractMovies(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            if (cursor.moveToPosition(i)) {
                int apiId = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_API_ID));
                String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                byte[] posterBlob = cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER));
                Bitmap poster = BitmapFactory.decodeByteArray(posterBlob, 0, posterBlob.length);
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
                String voteAverage = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
                String description = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_DESCRIPTION));

                Movie movie = new Movie(apiId, title, poster, releaseDate, voteAverage, description);
                movies.add(movie);
            }
        }

        return movies;
    }
}
