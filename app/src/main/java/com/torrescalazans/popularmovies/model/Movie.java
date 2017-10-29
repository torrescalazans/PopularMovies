package com.torrescalazans.popularmovies.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by jose torres on 10/22/17.
 */

public class Movie implements Serializable {

    private static final long versionUID = 1L;

    private final String mId;
    private final String mTitle;
    private final String mOriginalTitle;
    private final String mOverview;
    private final String mReleaseDate;
    private final String mOriginalLanguage;
    private final String mPosterPath;
    private final String mBackdropPath;
    private final String mPosterPathFullUrl;
    private final boolean mAdult;
    private final boolean mVideo;
    private final int[] mGenreIds;
    private final double mPopularity;
    private final double mVoteAverage;
    private final long mVoteCount;

    public Movie(String id, String title, String originalTitle, String overview, String releaseDate,
                 String originalLanguage, String posterPath, String posterPathFullUrl,
                 String backdropPath, boolean adult, boolean video, int[] genreIds, double popularity,
                 double voteAverage, long voteCount) {
        mId = id;
        mTitle = title;
        mOriginalTitle = originalTitle;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mOriginalLanguage = originalLanguage;
        mPosterPath = posterPath;
        mPosterPathFullUrl = posterPathFullUrl;
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
    public String getPosterPathFullUrl() {
        return mPosterPathFullUrl;
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
                ", mPosterPathFullUrl='" + mPosterPathFullUrl + '\'' +
                ", mAdult=" + mAdult +
                ", mVideo=" + mVideo +
                ", mGenreIds=" + Arrays.toString(mGenreIds) +
                ", mPopularity=" + mPopularity +
                ", mVoteAverage=" + mVoteAverage +
                ", mVoteCount=" + mVoteCount +
                '}';
    }
}
