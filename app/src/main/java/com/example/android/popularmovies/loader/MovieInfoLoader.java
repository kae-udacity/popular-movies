package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.database.MovieContract.MovieEntry;

/**
 * Starts a {@link android.content.Loader} to fetch the movie info from the database in the background.
 */

public class MovieInfoLoader extends AsyncTaskLoader<Movie> {

    private int apiId;

    public MovieInfoLoader(Context context, int apiId) {
        super(context);
        this.apiId = apiId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Movie loadInBackground() {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        Uri uri = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(MovieEntry.COLUMN_API_ID)
                .appendPath(String.valueOf(apiId))
                .build();
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);

        Movie movie = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                byte[] posterBlob = cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER));
                Bitmap poster = BitmapFactory.decodeByteArray(posterBlob, 0, posterBlob.length);
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
                String voteAverage = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
                String description = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_DESCRIPTION));

                movie = new Movie(apiId, title, poster, releaseDate, voteAverage, description);
            }
            cursor.close();
        }

        return movie;
    }
}
