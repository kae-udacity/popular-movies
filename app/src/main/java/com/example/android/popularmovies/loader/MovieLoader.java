package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.util.NetworkUtils;

import java.util.List;

/**
 * Starts a {@link android.content.Loader} to fetch the movies in the background.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String url;

    public MovieLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        return NetworkUtils.fetchMovies(getContext(), url);
    }
}
