package com.example.android.popularmovies.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import com.example.android.popularmovies.loader.MovieLoader;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterOnClickHandler {

    private ActivityMainBinding binding;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        movieAdapter = new MovieAdapter(this);
        binding.rvMovies.setAdapter(movieAdapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvMovies.setLayoutManager(layoutManager);

        if (isOnline()) {
            binding.rvMovies.setVisibility(View.VISIBLE);
            binding.tvEmptyView.setVisibility(View.GONE);
            getLoaderManager().initLoader(0, null, this);
        } else {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.rvMovies.setVisibility(View.GONE);
            binding.tvEmptyView.setText(R.string.no_internet_connection);
            binding.tvEmptyView.setVisibility(View.VISIBLE);
        }
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
    public MovieLoader onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sortBy = sharedPrefs.getString(
                getString(R.string.settings_sort_by_default_key),
                getString(R.string.settings_sort_by_default_value)
        );
        Uri baseUri = Uri.parse(getString(R.string.base_request_url));

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendPath(sortBy);
        uriBuilder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_key_value));
        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        binding.loadingIndicator.setVisibility(View.GONE);
        movieAdapter.clear();

        if (movies != null && !movies.isEmpty()) {
            binding.rvMovies.setVisibility(View.VISIBLE);
            binding.tvEmptyView.setVisibility(View.GONE);

            movieAdapter.setMovies(movies);
            movieAdapter.notifyDataSetChanged();
        } else {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.rvMovies.setVisibility(View.GONE);
            binding.tvEmptyView.setText(R.string.no_movies_found);
            binding.tvEmptyView.setVisibility(View.VISIBLE);
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

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
