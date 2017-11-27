package com.example.android.popularmovies.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Provides helper methods for creating a HTTP request to fetch the data from the API.
 * When the JSON is retrieved it extracts the relevant information and stores it in
 * the appropriate POJO - {@link Movie}, {@link Trailer} or {@link Review}.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static List<Movie> fetchMovies(Context context, String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }

        URL url = getUrl(context, requestUrl);
        String jsonResponse = getResponse(context, url);
        return extractMovies(context, jsonResponse);
    }

    public static List<Trailer> fetchTrailers(Context context, String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }

        URL url = getUrl(context, requestUrl);
        String jsonResponse = getResponse(context, url);
        return extractTrailers(context, jsonResponse);
    }

    public static List<Review> fetchReviews(Context context, String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }

        URL url = getUrl(context, requestUrl);
        String jsonResponse = getResponse(context, url);
        return extractReviews(context, jsonResponse);
    }

    @Nullable
    private static URL getUrl(Context context, String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, context.getString(R.string.error_creating_url), e);
        }
        return url;
    }

    private static String getResponse(Context context, URL url) {
        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponse = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(context.getString(R.string.get));
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFrom(inputStream);
            } else {
                Log.e(TAG, context.getString(R.string.error_retrieving_data_response_code) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, context.getString(R.string.error_retrieving_data), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, context.getString(R.string.error_closing_input_stream), e);
                }
            }
        }
        return jsonResponse;
    }

    private static String readFrom(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream == null) {
            return null;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    private static List<Movie> extractMovies(Context context, String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            if (baseJsonResponse.has(context.getString(R.string.results))) {
                JSONArray results = baseJsonResponse.getJSONArray(context.getString(R.string.results));
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieJson = results.getJSONObject(i);
                    Movie movie = getMovie(context, movieJson);
                    movies.add(movie);
                }
            } else {
                Movie movie = getMovie(context, baseJsonResponse);
                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e(TAG, context.getString(R.string.error_parsing_response), e);
        }

        return movies;
    }

    private static Movie getMovie(Context context, JSONObject movieJson) throws JSONException {
        int apiId = movieJson.getInt(context.getString(R.string.apiId));
        String title = movieJson.getString(context.getString(R.string.title));
        String posterPath = movieJson.getString(context.getString(R.string.poster_path)).substring(1);
        Uri uri = NetworkUtils.getImageUri(context, posterPath);
        Bitmap poster = null;
        try {
            poster = Picasso.with(context).load(uri).get();
        } catch (IOException e) {
            Log.e(TAG, context.getString(R.string.error_loading_poster), e);
        }

        String releaseDate = movieJson.getString(context.getString(R.string.release_date));
        String voteAverage = movieJson.getString(context.getString(R.string.vote_average));
        String description = movieJson.getString(context.getString(R.string.overview));

        return new Movie(apiId, title, poster, releaseDate, voteAverage, description);
    }

    private static Uri getImageUri(Context context, String posterPath) {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme(context.getString(R.string.https))
                .authority(context.getString(R.string.image_authority))
                .appendPath(context.getString(R.string.t))
                .appendPath(context.getString(R.string.p))
                .appendPath(context.getString(R.string.image_size_w185))
                .appendPath(posterPath)
                .build();
    }

    private static List<Trailer> extractTrailers(Context context, String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Trailer> trailers = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray results = baseJsonResponse.getJSONArray(context.getString(R.string.results));
            for (int i = 0; i < results.length(); i++) {
                JSONObject videoJson = results.getJSONObject(i);
                String name = videoJson.getString(context.getString(R.string.name));
                String key = videoJson.getString(context.getString(R.string.key));
                trailers.add(new Trailer(name, key));
            }

        } catch (JSONException e) {
            Log.e(TAG, context.getString(R.string.error_parsing_response), e);
        }

        return trailers;
    }

    private static List<Review> extractReviews(Context context, String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Review> reviews = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray results = baseJsonResponse.getJSONArray(context.getString(R.string.results));
            for (int i = 0; i < results.length(); i++) {
                JSONObject reviewJson = results.getJSONObject(i);
                String author = reviewJson.getString(context.getString(R.string.author));
                String content = reviewJson.getString(context.getString(R.string.content));
                reviews.add(new Review(author, content));
            }

        } catch (JSONException e) {
            Log.e(TAG, context.getString(R.string.error_parsing_response), e);
        }

        return reviews;
    }

    @NonNull
    public static String buildUrl(Context context, Uri baseUri, String... paths) {
        Uri.Builder uriBuilder;
        uriBuilder = baseUri.buildUpon();
        for (String path : paths) {
            uriBuilder.appendPath(path);
        }
        uriBuilder.appendQueryParameter(context.getString(R.string.api_key), context.getString(R.string.API_KEY_VALUE));
        return uriBuilder.toString();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
