package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.database.MovieContract.MovieEntry;
import com.example.android.popularmovies.util.DatabaseUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import java.util.List;

/**
 * Starts a {@link android.content.Loader} to fetch movies in the background.
 */

public class MovieListLoader extends AsyncTaskLoader<List<Movie>> {

    private String sortBy;

    public MovieListLoader(Context context, String sortBy) {
        super(context);
        this.sortBy = sortBy;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        Context context = getContext();

        Uri baseUri = Uri.parse(context.getString(R.string.base_request_url));
        if (sortBy.equals(context.getString(R.string.settings_favourites_value))) {
            Cursor cursor = context.getContentResolver().query(
                    MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            List<Movie> movies = DatabaseUtils.extractMovies(cursor);

            if (cursor != null) {
                cursor.close();
            }
            return movies;
        } else {
            String url = NetworkUtils.buildUrl(context, baseUri, sortBy);
            return NetworkUtils.fetchMovies(context, url);
        }
    }
}
