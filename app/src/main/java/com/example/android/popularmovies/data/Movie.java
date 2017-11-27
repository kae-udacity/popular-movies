package com.example.android.popularmovies.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores the data of the movie.
 */

public class Movie implements Parcelable {

    private int apiId;
    private String title;
    private Bitmap poster;
    private String releaseDate;
    private String voteAverage;
    private String description;

    public Movie(int apiId, String title, Bitmap poster, String releaseDate, String voteAverage, String description) {
        this.apiId = apiId;
        this.title = title;
        this.poster = poster;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.description = description;
    }

    private Movie(Parcel in) {
        apiId = in.readInt();
        title = in.readString();
        poster = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        releaseDate = in.readString();
        voteAverage = in.readString();
        description = in.readString();
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

    public int getApiId() {
        return apiId;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(apiId);
        parcel.writeString(title);
        parcel.writeValue(poster);
        parcel.writeString(releaseDate);
        parcel.writeString(voteAverage);
        parcel.writeString(description);
    }

    public int describeContents() {
        return 0;
    }
}
