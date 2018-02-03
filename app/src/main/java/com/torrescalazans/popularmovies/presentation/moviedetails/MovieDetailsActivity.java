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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.database.PopularMoviesContract;
import com.torrescalazans.popularmovies.model.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private TextView mMovieOriginalTitleTextView;
    private ImageView mMoviePosterThumbnail;
    private TextView mMovieSynopsisTextView;
    private TextView mMovieUserRating;
    private TextView mMovieReleaseDate;

    private Movie mMovie;

    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovieOriginalTitleTextView = findViewById(R.id.tv_movie_original_title);
        mMoviePosterThumbnail = findViewById(R.id.iv_movie_poster_thumbnail);
        mMovieSynopsisTextView = findViewById(R.id.tv_movie_synopsis);
        mMovieUserRating = findViewById(R.id.tv_movie_user_rating);
        mMovieReleaseDate = findViewById(R.id.tv_movie_release_date);

        if ((getIntent() != null) && getIntent().hasExtra("movie_data")) {
            mMovie = (Movie) getIntent().getParcelableExtra("movie_data");
        } else {
            throw new UnsupportedOperationException("Missing intent extra: movie_data");
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

        if (favoriteCursor.getCount() != 0) {
            isFavorite = true;
        }

        populateMovieDetails();
    }

    private void populateMovieDetails() {
        Log.d(TAG, "populateMovieDetails");

        mMovieOriginalTitleTextView.setText(mMovie.getOriginalTitle());

        // TODO save blob in sqlite
        Picasso.with(getApplicationContext()).load(mMovie.getPosterUrl())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mMoviePosterThumbnail);

        mMovieSynopsisTextView.setText(mMovie.getOverview());

        mMovieUserRating.setText(
                getResources().getString(
                        R.string.movie_details_activity_movie_user_rating,
                        mMovie.getVoteAverage()));

        mMovieReleaseDate.setText(
                getResources().getString(
                        R.string.movie_details_activity_movie_release_date,
                        mMovie.getReleaseDate()));
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
                contentValues.put(
                        PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID,
                        mMovie.getId());

                contentValues.put(
                        PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE,
                        mMovie.getOriginalTitle());

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
}
