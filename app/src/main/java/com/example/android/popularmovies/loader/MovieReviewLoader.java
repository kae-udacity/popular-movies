package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.util.NetworkUtils;

import java.util.List;

/**
 * Starts a {@link android.content.Loader} to fetch the movie reviews in the background.
 */

public class MovieReviewLoader extends AsyncTaskLoader<List<Review>> {

    private int apiId;

    public MovieReviewLoader(Context context, int apiId) {
        super(context);
        this.apiId = apiId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Review> loadInBackground() {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        Uri baseUri = Uri.parse(context.getString(R.string.base_request_url));
        String url = NetworkUtils.buildUrl(context, baseUri, String.valueOf(apiId), context.getString(R.string.reviews));
        return NetworkUtils.fetchReviews(context, url);
    }
}
