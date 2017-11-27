package com.example.android.popularmovies.activity;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.database.MovieContract.MovieEntry;
import com.example.android.popularmovies.databinding.ActivityDetailsBinding;
import com.example.android.popularmovies.loader.DeleteFavouriteLoader;
import com.example.android.popularmovies.loader.MovieInfoLoader;
import com.example.android.popularmovies.loader.MovieReviewLoader;
import com.example.android.popularmovies.loader.MovieTrailerLoader;
import com.example.android.popularmovies.util.DateUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private static final int MOVIE_INFO_LOADER_ID = 2;
    private static final int DELETE_FAVOURITE_LOADER_ID = 3;
    private static final int TRAILER_LOADER_ID = 4;
    private static final int REVIEW_LOADER_ID = 5;
    private ActivityDetailsBinding binding;
    private Movie movie;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private LoaderCallbacks<Movie> movieInfoLoaderCallbacks = new LoaderCallbacks<Movie>() {
        @Override
        public Loader<Movie> onCreateLoader(int i, Bundle bundle) {
            return new MovieInfoLoader(DetailsActivity.this, movie.getApiId());
        }

        @Override
        public void onLoadFinished(Loader<Movie> loader, Movie movie) {
            if (movie != null) {
                binding.imageButtonDetailsStar.setImageResource(R.drawable.star);
                DetailsActivity.this.movie = movie;
            }
            setupMovieViews();
        }

        @Override
        public void onLoaderReset(Loader<Movie> loader) {

        }
    };

    private LoaderCallbacks<Integer> deleteFavouriteLoaderCallbacks = new LoaderCallbacks<Integer>() {
        @Override
        public DeleteFavouriteLoader onCreateLoader(int i, Bundle bundle) {
            return new DeleteFavouriteLoader(DetailsActivity.this, movie.getApiId());
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer rowsDeleted) {
            if (rowsDeleted > 0) {
                binding.imageButtonDetailsStar.setImageResource(R.drawable.star_border);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
                contentValues.put(MovieEntry.COLUMN_API_ID, movie.getApiId());

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                movie.getPoster().compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                byte[] posterBlob = outputStream.toByteArray();
                contentValues.put(MovieEntry.COLUMN_POSTER, posterBlob);
                contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                contentValues.put(MovieEntry.COLUMN_DESCRIPTION, movie.getDescription());

                Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);

                if (uri != null) {
                    binding.imageButtonDetailsStar.setImageResource(R.drawable.star);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {

        }
    };

    private LoaderCallbacks<List<Trailer>> trailerLoaderCallbacks = new LoaderCallbacks<List<Trailer>>() {

        @Override
        public MovieTrailerLoader onCreateLoader(int i, Bundle bundle) {
            return new MovieTrailerLoader(DetailsActivity.this, movie.getApiId());
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailers) {
            trailerAdapter.clear();
            trailerAdapter.setTrailers(trailers);
            trailerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {
            trailerAdapter.clear();
            trailerAdapter.notifyDataSetChanged();
        }
    };

    private LoaderCallbacks<List<Review>> reviewLoaderCallbacks = new LoaderCallbacks<List<Review>>() {
        @Override
        public Loader<List<Review>> onCreateLoader(int i, Bundle bundle) {
            return new MovieReviewLoader(DetailsActivity.this, movie.getApiId());
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
            reviewAdapter.clear();
            reviewAdapter.setReviews(reviews);
            reviewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
            reviewAdapter.clear();
            reviewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        movie = getIntent().getParcelableExtra(getString(R.string.movie));
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.detailsContainer.setVisibility(View.GONE);
        getLoaderManager().initLoader(MOVIE_INFO_LOADER_ID, null, movieInfoLoaderCallbacks);

        if (NetworkUtils.isOnline(this)) {
            RecyclerView.LayoutManager trailerLayoutManager = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            trailerAdapter = new TrailerAdapter(this);
            binding.recyclerViewTrailers.setAdapter(trailerAdapter);
            binding.recyclerViewTrailers.setLayoutManager(trailerLayoutManager);
            getLoaderManager().initLoader(TRAILER_LOADER_ID, null, trailerLoaderCallbacks);

            RecyclerView.LayoutManager reviewLayoutManager = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            reviewAdapter = new ReviewAdapter();
            binding.recyclerViewReviews.setAdapter(reviewAdapter);
            binding.recyclerViewReviews.setLayoutManager(reviewLayoutManager);
            getLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewLoaderCallbacks);
        }
    }

    public void updateFavourites(View view) {
        if (movie == null) {
            return;
        }

        LoaderManager loaderManager = getLoaderManager();
        if (loaderManager.getLoader(DELETE_FAVOURITE_LOADER_ID) == null) {
            getLoaderManager().initLoader(DELETE_FAVOURITE_LOADER_ID, null, deleteFavouriteLoaderCallbacks);
        } else {
            getLoaderManager().restartLoader(DELETE_FAVOURITE_LOADER_ID, null, deleteFavouriteLoaderCallbacks);
        }
    }

    private void setupMovieViews() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.detailsContainer.setVisibility(View.VISIBLE);
        binding.textViewDetailsTitle.setText(movie.getTitle());

        binding.imageViewDetailsPoster.setImageBitmap(movie.getPoster());
        Date date = DateUtils.getDate(this, getString(R.string.yyyy_mm_dd), movie.getReleaseDate());
        String formattedDate = new SimpleDateFormat(getString(R.string.lll_dd_yyyy), Locale.getDefault()).format(date);
        binding.textViewDetailsReleaseDate.setText(formattedDate);
        binding.textViewDetailsVoteAverage.setText(movie.getVoteAverage());
        binding.textViewDetailsDescription.setText(movie.getDescription());
    }

    @Override
    public void onClick(Trailer trailer) {
        String key = trailer.getKey();
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_protocol) + key));
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_url) + key));
            startActivity(webIntent);
        }
    }
}
