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

package com.torrescalazans.popularmovies.presentation.main;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.model.Movie;

import java.util.List;

/**
 * {@link MovieAdapter} exposes a list of favorites
 * from a {@link Cursor} to a {@link RecyclerView}.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final MovieAdapterOnClickHandler mOnClickHandler;

    private List<Movie> mMoviesList;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param onClickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = view.findViewById(R.id.iv_movie_item);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMoviesList.get(adapterPosition);
            mOnClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new MovieAdapterViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
//        Context context = movieAdapterViewHolder.mMovieImageView.getContext();
//
//        // get to the right location in the cursor
//        mCursor.moveToPosition(position);
//
//        final int movieIdIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID);
//        final int movieTitleIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE);
//        final int movieOriginalTitleIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE);
//        final int movieOverviewIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW);
//        final int movieReleaseDateIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE);
//        final int movieOriginalLanguageIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE);
//        final int moviePosterPathIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH);
//        final int movieBackdropPathIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH);
//        final int moviePosterUrlIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL);
//        final int movieAdultIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ADULT);
//        final int movieVideoIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO);
//        final int movieGenreIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS);
//        final int moviePopularityIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY);
//        final int movieVoteAverageIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE);
//        final int movieVoteCountIndex = mCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT);
//
//        // Determine the values of the wanted data
//        final String movieId = mCursor.getString(movieIdIndex);
//        final String movieTitle = mCursor.getString(movieTitleIndex);
//        final String movieOriginalTitle = mCursor.getString(movieOriginalTitleIndex);
//
//        final String moviePosterUrl = mCursor.getString(moviePosterUrlIndex);
//
//        Picasso.with(context).load(moviePosterUrl)
//                .error(R.drawable.placeholder)
//                .placeholder(R.drawable.placeholder)
//                .into(movieAdapterViewHolder.mMovieImageView);
//    }

    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        Context context = movieAdapterViewHolder.mMovieImageView.getContext();
        Movie movie = mMoviesList.get(position);

        Picasso.with(context).load(movie.getPosterUrl())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(movieAdapterViewHolder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        return (null == mMoviesList ? 0 : mMoviesList.size());
    }

    public void updateData(List<Movie> moviesList) {
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }
}
