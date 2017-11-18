package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores the data of the movie.
 */

public class Movie implements Parcelable {

    private String title;
    private String posterPath;
    private String description;
    private double voteAverage;
    private String releaseDate;

    public Movie(String title, String posterPath, String description, double voteAverage, String releaseDate) {
        this.title = title;
        this.posterPath = posterPath;
        this.description = description;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        description = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getDescription() {
        return description;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(description);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
    }
}
