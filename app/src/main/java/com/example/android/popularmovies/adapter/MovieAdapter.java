package com.example.android.popularmovies.adapter;

import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;

import java.util.List;

/**
 * Creates and populates views for the list of movies in the grid.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> movies;
    private MovieAdapterOnClickHandler clickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_grid_item, parent, false);

        int height;
        if (parent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            height = parent.getHeight() / 2;
        } else {
            height = (int) (parent.getHeight() * 1.5);
        }
        int width = parent.getWidth() / 2;

        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        if (movies == null) {
            return;
        }
        Movie movie = movies.get(position);
        holder.moviePosterImageView.setImageBitmap(movie.getPoster());
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void clear() {
        if (movies == null) {
            return;
        }
        movies.clear();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moviePosterImageView;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.image_view_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            clickHandler.onClick(movies.get(index));
        }
    }
}
