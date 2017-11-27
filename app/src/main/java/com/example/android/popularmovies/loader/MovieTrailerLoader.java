package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.util.NetworkUtils;

import java.util.List;

/**
 * Starts a {@link android.content.Loader} to fetch the movie trailers in the background.
 */

public class MovieTrailerLoader extends AsyncTaskLoader<List<Trailer>> {

    private int apiId;

    public MovieTrailerLoader(Context context, int apiId) {
        super(context);
        this.apiId = apiId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Trailer> loadInBackground() {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        Uri baseUri = Uri.parse(context.getString(R.string.base_request_url));
        String url = NetworkUtils.buildUrl(context, baseUri, String.valueOf(apiId), context.getString(R.string.videos));
        return NetworkUtils.fetchTrailers(context, url);
    }
}
