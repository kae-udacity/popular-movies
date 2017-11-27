package com.example.android.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Review;

import java.util.List;

/**
 * Creates and populates views for the list of reviews.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<Review> reviews;

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_review, parent, false);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        if (reviews == null) {
            return;
        }
        Review review = reviews.get(position);
        holder.content.setText(review.getContent());
        holder.author.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        return reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void clear() {
        if (reviews == null) {
            return;
        }
        reviews.clear();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        TextView author;

        ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.text_view_review_content);
            author = itemView.findViewById(R.id.text_view_review_author);
        }
    }
}
