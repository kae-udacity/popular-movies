package com.example.android.popularmovies.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.ActivityDetailsBinding;
import com.example.android.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Movie movie = getIntent().getParcelableExtra(getString(R.string.movie));
        if (movie != null) {
            binding.detailsMovieTitle.setText(movie.getTitle());

            String formattedDate = formatDate(movie.getReleaseDate());
            binding.detailsMovieReleaseDate.setText(formattedDate);
            binding.detailsMovieVoteAverage.setText(String.valueOf(movie.getVoteAverage()));
            binding.detailsMovieDescription.setText(movie.getDescription());

            Uri uri = NetworkUtils.getImageUri(this, movie.getPosterPath());
            Picasso.with(this).load(uri).into(binding.detailsMoviePoster);
        }
    }

    private String formatDate(String unformattedDate) {
        String formattedDate = null;
        try {
            Date date = new SimpleDateFormat(getString(R.string.yyyy_mm_dd), Locale.getDefault()).parse(unformattedDate);
            formattedDate = new SimpleDateFormat(getString(R.string.lll_dd_yyyy), Locale.getDefault()).format(date);
        } catch (ParseException e) {
            Log.e(TAG, getString(R.string.error_parsing_date), e);
        }

        return formattedDate;
    }
}
