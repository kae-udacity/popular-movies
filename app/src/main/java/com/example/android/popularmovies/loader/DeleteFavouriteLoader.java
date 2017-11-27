package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.database.MovieContract;

/**
 * Starts a {@link android.content.Loader} to delete the favourite from the database if it exists.
 */

public class DeleteFavouriteLoader extends AsyncTaskLoader<Integer> {

    private int apiId;

    public DeleteFavouriteLoader(Context context, int apiId) {
        super(context);
        this.apiId = apiId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Integer loadInBackground() {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(MovieContract.MovieEntry.COLUMN_API_ID)
                .appendPath(String.valueOf(apiId))
                .build();

        return getContext().getContentResolver().delete(uri, null, null);
    }
}
