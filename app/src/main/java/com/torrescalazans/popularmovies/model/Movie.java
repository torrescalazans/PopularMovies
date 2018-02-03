/*
 * Copyright (C) 2018 Jose Torres
 *
 * This file is part of PopularMovies.
 *
 * PopularMovies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * PopularMovies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.torrescalazans.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Movie implements Parcelable {

    private final String mId;
    private final String mTitle;
    private final String mOriginalTitle;
    private final String mOverview;
    private final String mReleaseDate;
    private final String mOriginalLanguage;
    private final String mPosterPath;
    private final String mBackdropPath;
    private final String mPosterUrl;
    private final boolean mAdult;
    private final boolean mVideo;
    private int[] mGenreIds; // TODO initialize
    private final double mPopularity;
    private final double mVoteAverage;
    private final long mVoteCount;

    public Movie(String id, String title, String originalTitle, String overview, String releaseDate,
                 String originalLanguage, String posterPath, String posterUrl,
                 String backdropPath, boolean adult, boolean video, int[] genreIds, double popularity,
                 double voteAverage, long voteCount) {
        mId = id;
        mTitle = title;
        mOriginalTitle = originalTitle;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mOriginalLanguage = originalLanguage;
        mPosterPath = posterPath;
        mPosterUrl = posterUrl;
        mBackdropPath = backdropPath;
        mAdult = adult;
        mVideo = video;
        mGenreIds = genreIds;
        mPopularity = popularity;
        mVoteAverage = voteAverage;
        mVoteCount = voteCount;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getPosterPath() {
        return mPosterPath;
    }
    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public boolean isAdult() {
        return mAdult;
    }

    public boolean isVideo() {
        return mVideo;
    }

    public int[] getGenreIds() {
        return mGenreIds;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public long getVoteCount() {
        return mVoteCount;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mOriginalTitle='" + mOriginalTitle + '\'' +
                ", mOverview='" + mOverview + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mOriginalLanguage='" + mOriginalLanguage + '\'' +
                ", mPosterPath='" + mPosterPath + '\'' +
                ", mBackdropPath='" + mBackdropPath + '\'' +
                ", mPosterPathFullUrl='" + mPosterUrl + '\'' +
                ", mAdult=" + mAdult +
                ", mVideo=" + mVideo +
                ", mGenreIds=" + Arrays.toString(mGenreIds) +
                ", mPopularity=" + mPopularity +
                ", mVoteAverage=" + mVoteAverage +
                ", mVoteCount=" + mVoteCount +
                '}';
    }

    protected Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mOriginalLanguage = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mPosterUrl = in.readString();
        mAdult = in.readByte() != 0x00;
        mVideo = in.readByte() != 0x00;
        mPopularity = in.readDouble();
        mVoteAverage = in.readDouble();
        mVoteCount = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeString(mOriginalLanguage);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mPosterUrl);
        dest.writeByte((byte) (mAdult ? 0x01 : 0x00));
        dest.writeByte((byte) (mVideo ? 0x01 : 0x00));
        dest.writeDouble(mPopularity);
        dest.writeDouble(mVoteAverage);
        dest.writeLong(mVoteCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
