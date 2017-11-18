package com.example.android.popularmovies.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;

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

/**
 * Creates a HTTP request to fetch the movies. When the data is retrieved it extracts the
 * relevant information and stores it in a {@link List} of {@link Movie} objects.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static List<Movie> fetchMovies(Context context, String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }

        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, context.getString(R.string.error_creating_url), e);
        }

        String jsonResponse = getResponse(context, url);
        return extractMovies(context, jsonResponse);
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
            JSONArray results = baseJsonResponse.getJSONArray(context.getString(R.string.results));

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieJson = results.getJSONObject(i);
                Movie movie = getMovie(context, movieJson);
                movies.add(movie);
            }
        } catch (JSONException e) {
            Log.e(TAG, context.getString(R.string.error_parsing_response), e);
        }

        return movies;
    }

    private static Movie getMovie(Context context, JSONObject movieJson) throws JSONException {
        String title = movieJson.getString(context.getString(R.string.title));
        String posterPath = movieJson.getString(context.getString(R.string.poster_path)).substring(1);
        String description = movieJson.getString(context.getString(R.string.overview));
        double voteAverage = movieJson.getDouble(context.getString(R.string.vote_average));
        String releaseDate = movieJson.getString(context.getString(R.string.release_date));

        return new Movie(title, posterPath, description, voteAverage, releaseDate);
    }

    public static Uri getImageUri(Context context, String posterPath) {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme(context.getString(R.string.https))
                .authority(context.getString(R.string.image_authority))
                .appendPath(context.getString(R.string.t))
                .appendPath(context.getString(R.string.p))
                .appendPath(context.getString(R.string.image_size_w185))
                .appendPath(posterPath)
                .build();
    }
}
