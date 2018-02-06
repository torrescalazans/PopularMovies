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

package com.torrescalazans.popularmovies.database;

import android.database.Cursor;

import com.torrescalazans.popularmovies.model.Movie;

import java.util.ArrayList;

public class DatabaseUtils {

    public static ArrayList<Movie> arrayListFromCursor(Cursor cursor) {

        ArrayList<Movie> moviesList = new ArrayList<>();

        final int movieIdIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID);
        final int movieTitleIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE);
        final int movieOriginalTitleIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE);
        final int movieOverviewIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW);
        final int movieReleaseDateIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE);
        final int movieOriginalLanguageIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE);
        final int moviePosterPathIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH);
        final int movieBackdropPathIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH);
        final int moviePosterUrlIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL);
        final int movieAdultIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ADULT);
        final int movieVideoIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO);
        final int movieGenreIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS);
        final int moviePopularityIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY);
        final int movieVoteAverageIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE);
        final int movieVoteCountIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            // TODO convert string to int[]
            int[] genreIds = new int[3];

            Movie movie = new Movie(
                cursor.getString(movieIdIndex),
                cursor.getString(movieTitleIndex),
                cursor.getString(movieOriginalTitleIndex),
                cursor.getString(movieOverviewIndex),
                cursor.getString(movieReleaseDateIndex),
                cursor.getString(movieOriginalLanguageIndex),
                cursor.getString(moviePosterPathIndex),
                cursor.getString(moviePosterUrlIndex),
                cursor.getString(movieBackdropPathIndex),
                (cursor.getInt(movieAdultIndex) > 0),
                (cursor.getInt(movieVideoIndex) > 0),
                genreIds,
                cursor.getFloat(moviePopularityIndex),
                cursor.getFloat(movieVoteAverageIndex),
                cursor.getInt(movieVoteCountIndex)
            );

            moviesList.add(movie);
        }

        return moviesList;
    }
}
