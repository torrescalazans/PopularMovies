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

package com.torrescalazans.popularmovies.presentation.moviedetails;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;
import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.database.PopularMoviesContract;
import com.torrescalazans.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.torrescalazans.popularmovies.model.Movie;
import com.torrescalazans.popularmovies.model.Review;
import com.torrescalazans.popularmovies.model.Trailer;
import com.torrescalazans.popularmovies.network.communication.NetworkManager;
import com.torrescalazans.popularmovies.network.communication.NetworkResultReceiver;

import java.util.ArrayList;

import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_ERROR;
import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_REVIEWS_FINISHED;
import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_RUNNING;
import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_TRAILERS_FINISHED;

public class MovieDetailsActivity extends AppCompatActivity
        implements NetworkResultReceiver.Receiver,
        TrailerAdapter.TrailerAdapterOnClickHandler {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    private ActivityMovieDetailsBinding mMovieDetaislBinding;

    private Movie mMovie;
    private boolean isFavorite;

    private ArrayList<Trailer> mTrailerArrayList;
    private TrailerAdapter mTrailerAdapter;

    private ArrayList<Review> mReviewsArrayList;
    private ReviewAdapter mReviewAdapter;

    private NetworkResultReceiver mNetworkResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        mMovieDetaislBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        mNetworkResultReceiver = new NetworkResultReceiver(new Handler());
        mNetworkResultReceiver.setReceiver(this);

        populateMovieDetails();
        populateMovieTrailers();
        populateMovieReviews();
    }

    private void populateMovieDetails() {
        Log.d(TAG, "populateMovieDetails");

        if ((getIntent() != null) && getIntent().hasExtra("movie_data")) {
            mMovie = getIntent().getParcelableExtra("movie_data");
        } else {
            throw new RuntimeException("Missing intent extra: movie_data");
        }

        /* Perform the ContentProvider query */
        String movieId = mMovie.getId();
        String[] selectionArguments = new String[]{movieId};

        Cursor favoriteCursor = getContentResolver().query(
                PopularMoviesContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(mMovie.getId()).build(),
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID,
                /* Values for "where" clause */
                selectionArguments,
                /* Sort order to return in Cursor */
                null);

        isFavorite = favoriteCursor.getCount() != 0;

        mMovieDetaislBinding.titleInfo.movieTitle.setText(mMovie.getTitle());

        Picasso.with(getApplicationContext()).load(mMovie.getPosterUrl())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mMovieDetaislBinding.posterInfo.ivMoviePoster);

        mMovieDetaislBinding.posterInfo.tvReleaseDate.setText(mMovie.getReleaseDate().split("-")[0]);

        mMovieDetaislBinding.posterInfo.tvVoteAverage.setText(getResources().getString(
                R.string.movie_details_activity_movie_vote_average,
                mMovie.getVoteAverage()));

        mMovieDetaislBinding.posterInfo.tvOverview.setText(mMovie.getOverview());
    }

    private void populateMovieTrailers() {
        Log.d(TAG, "populateMovieTrailers");

        mMovieDetaislBinding.trailersList.rvTrailersList.setHasFixedSize(true);

        mTrailerAdapter = new TrailerAdapter(this);
        mMovieDetaislBinding.trailersList.rvTrailersList.setAdapter(mTrailerAdapter);

        mMovieDetaislBinding.trailersList.rvTrailersList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );

        NetworkManager.startSync(this, mNetworkResultReceiver,
                NetworkManager.RequestType.VIDEOS, Integer.valueOf(mMovie.getId()));
    }

    private void populateMovieReviews() {
        Log.d(TAG, "populateMovieReviews");

        mMovieDetaislBinding.reviewsList.rvReviewsList.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter();
        mMovieDetaislBinding.reviewsList.rvReviewsList.setAdapter(mReviewAdapter);

        mMovieDetaislBinding.reviewsList.rvReviewsList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );

        NetworkManager.startSync(this, mNetworkResultReceiver,
                NetworkManager.RequestType.REVIEWS, Integer.valueOf(mMovie.getId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_movie_details_actions, menu);

        // Check if it is already stored and update icon accordingly
        if (isFavorite) {
            menu.findItem(R.id.movie_details_action_favorite).setIcon(
                    R.drawable.ic_action_favorite_white_48dp);
        } else {
            menu.findItem(R.id.movie_details_action_favorite).setIcon(
                    R.drawable.ic_action_favorite_border_white_48dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        int id = item.getItemId();

        if (id == R.id.movie_details_action_favorite) {
            Log.d(TAG, "onOptionsItemSelected - action favorite");

            if (isFavorite) {
                Log.d(TAG, "onOptionsItemSelected - remove from favorites list");

                Uri uriToDelete = PopularMoviesContract.FavoriteEntry.CONTENT_URI.buildUpon().
                        appendPath(mMovie.getId()).build();

                int favoriteDeleted = getContentResolver().delete(uriToDelete, null, null);

                if (favoriteDeleted != -1) {
                    isFavorite = false;
                    item.setIcon(R.drawable.ic_action_favorite_border_white_48dp);

                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.movie_details_activity_removed_from_favorites_list,
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            } else {
                Log.d(TAG, "onOptionsItemSelected - add to favorites list");

                // Insert new favorite data via a ContentResolver
                // Create new empty ContentValues object
                ContentValues contentValues = new ContentValues();

                // Put the movie information into the ContentValues
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID, mMovie.getId());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE, mMovie.getTitle());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE, mMovie.getOriginalTitle());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW, mMovie.getOverview());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE, mMovie.getOriginalLanguage());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH, mMovie.getPosterPath());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH, mMovie.getBackdropPath());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL, mMovie.getPosterUrl());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ADULT, mMovie.isAdult());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO, mMovie.isVideo());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS, "18,27,53"); // TODO convert string to int[]
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY, mMovie.getPopularity());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE, mMovie.getVoteAverage());
                contentValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT, mMovie.getVoteCount());

                // Insert the content values via a ContentResolver
                Uri uri = getContentResolver().insert(
                        PopularMoviesContract.FavoriteEntry.CONTENT_URI,
                        contentValues);

                if (uri != null) {
                    isFavorite = true;
                    item.setIcon(R.drawable.ic_action_favorite_white_48dp);

                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.movie_details_activity_added_as_favorites_list,
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case STATUS_RUNNING:
                Log.d(TAG, "onReceiveResult - resultCode: STATUS_RUNNING");

                // TODO add loading indicator inside trailers list view
                break;

            case STATUS_TRAILERS_FINISHED:
                Log.d(TAG, "onReceiveResult - resultCode: STATUS_TRAILERS_FINISHED");

                mTrailerArrayList = resultData.getParcelableArrayList("trailers_list");
                mTrailerAdapter.updateData(mTrailerArrayList);
                break;

            case STATUS_REVIEWS_FINISHED:
                Log.d(TAG, "onReceiveResult - resultCode: STATUS_REVIEWS_FINISHED");

                mReviewsArrayList = resultData.getParcelableArrayList("reviews_list");
                mReviewAdapter.updateData(mReviewsArrayList);
                break;

            case STATUS_ERROR:
                Log.d(TAG, "onReceiveResult - resultCode: STATUS_ERROR");

                // TODO add error message inside trailers or reviews list
                break;
        }
    }

    @Override
    public void onClick(Trailer trailer) {
        Log.d(TAG, "onClick: " + trailer);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        intent.putExtra("VIDEO_ID", trailer.getKey());
        startActivity(intent);
    }
}
