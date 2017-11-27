package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Trailer;

import java.util.List;

/**
 * Creates and populates views for the list of trailers.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private List<Trailer> trailers;
    private TrailerAdapterOnClickHandler clickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_trailer, parent, false);

        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerAdapterViewHolder holder, int position) {
        if (trailers == null) {
            return;
        }
        final Trailer trailer = trailers.get(position);
        holder.trailerName.setText(trailer.getName());
        holder.trailerShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.trailerShare.getContext();
                String url = context.getString(R.string.youtube_url) + trailer.getKey();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType(context.getString(R.string.type));
                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_link_using)));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.size();
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public void clear() {
        if (trailers == null) {
            return;
        }
        trailers.clear();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerName;
        ImageButton trailerShare;

        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.text_view_trailer_name);
            trailerShare = itemView.findViewById(R.id.image_button_share);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            clickHandler.onClick(trailers.get(index));
        }
    }
}
