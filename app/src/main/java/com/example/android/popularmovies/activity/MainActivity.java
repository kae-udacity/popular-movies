package com.example.android.popularmovies.activity;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.loader.MovieListLoader;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.util.NetworkUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MOVIE_LIST_LOADER_ID = 1;
    private ActivityMainBinding binding;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        movieAdapter = new MovieAdapter(this);
        binding.recyclerViewMovies.setAdapter(movieAdapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyclerViewMovies.setLayoutManager(layoutManager);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sortBy = sharedPrefs.getString(
                getString(R.string.settings_sort_by_default_key),
                getString(R.string.settings_sort_by_default_value)
        );
        if (NetworkUtils.isOnline(this) || sortBy.equals(getString(R.string.settings_favourites_value))) {
            binding.recyclerViewMovies.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
            getLoaderManager().initLoader(MOVIE_LIST_LOADER_ID, null, this);
        } else {
            setupEmptyView(R.string.no_internet_connection);
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public MovieListLoader onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sortBy = sharedPrefs.getString(
                getString(R.string.settings_sort_by_default_key),
                getString(R.string.settings_sort_by_default_value)
        );
        return new MovieListLoader(this, sortBy);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        binding.loadingIndicator.setVisibility(View.GONE);
        movieAdapter.clear();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sortBy = sharedPrefs.getString(
                getString(R.string.settings_sort_by_default_key),
                getString(R.string.settings_sort_by_default_value)
        );
        if (NetworkUtils.isOnline(this) || sortBy.equals(getString(R.string.settings_favourites_value))) {
            if (movies != null && !movies.isEmpty()) {
                binding.recyclerViewMovies.setVisibility(View.VISIBLE);
                binding.emptyView.setVisibility(View.GONE);

                movieAdapter.setMovies(movies);
                movieAdapter.notifyDataSetChanged();
            } else {
                int messageResourceId = R.string.no_movies_found;
                if (sortBy.equals(getString(R.string.settings_favourites_value))) {
                    messageResourceId = R.string.no_favourites_added;
                }
                setupEmptyView(messageResourceId);
            }
        } else {
            setupEmptyView(R.string.no_internet_connection);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        movieAdapter.clear();
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.movie), movie);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_sort_by_default_key))) {
            movieAdapter.clear();
            movieAdapter.notifyDataSetChanged();

            String sortBy = sharedPreferences.getString(key, getString(R.string.settings_sort_by_default_value));
            if (NetworkUtils.isOnline(this) || sortBy.equals(getString(R.string.settings_favourites_value))) {
                binding.loadingIndicator.setVisibility(View.VISIBLE);
                binding.recyclerViewMovies.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.GONE);
                getLoaderManager().restartLoader(MOVIE_LIST_LOADER_ID, null, this);
            } else {
                setupEmptyView(R.string.no_internet_connection);
            }
        }
    }

    private void setupEmptyView(int stringResourceId) {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.recyclerViewMovies.setVisibility(View.GONE);
        binding.emptyView.setText(stringResourceId);
        binding.emptyView.setVisibility(View.VISIBLE);
    }
}
