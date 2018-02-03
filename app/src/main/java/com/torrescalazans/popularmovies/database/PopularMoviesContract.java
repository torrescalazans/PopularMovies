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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 */
public class PopularMoviesContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.torrescalazans.popularmovies";

    /*
    * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    * the content provider for PopularMovies.
    */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
    * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that PopularMovies
    * can handle.
    */
    public static final String PATH_FAVORITES = "favorites";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PopularMoviesContract() {}

    /* Inner class that defines the table contents of the favorites table */
    public static final class FavoriteEntry implements BaseColumns {

        // FavoritesEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        /* Used internally as the name of our favorites table. */
        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_NAME_MOVIE_ORIGINAL_TITLE = "movie_original_title";
        public static final String COLUMN_NAME_MOVIE_OVERVIEW = "movie_overview";
        public static final String COLUMN_NAME_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE = "movie_original_language";
        public static final String COLUMN_NAME_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_NAME_MOVIE_BACKDROP_PATH = "movie_backdrop_path";
        public static final String COLUMN_NAME_MOVIE_POSTER_URL = "movie_poster_path_full_url";
        public static final String COLUMN_NAME_MOVIE_ADULT = "movie_adult";
        public static final String COLUMN_NAME_MOVIE_VIDEO = "movie_video";
        public static final String COLUMN_NAME_MOVIE_GENRE_IDS = "movie_genre_ids";
        public static final String COLUMN_NAME_MOVIE_POPULARITY = "movie_popularity";
        public static final String COLUMN_NAME_MOVIE_VOTE_AVERAGE = "movie_vote_average";
        public static final String COLUMN_NAME_MOVIE_VOTE_COUNT = "movie_vote_count";

        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        tasks
         - - - - - - - - - - - - - - - - - -
        | _id  | movie_id |     title       |
         - - - - - - - - - - - - - - - - - -
        |  1   |   1000   |  Movie title 01 |
         - - - - - - - - - - - - - - - - - -
        |  2   |   2000   |  Movie title 02 |
         - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - -
        |  50  |   2000   |  Movie title 50 |
         - - - - - - - - - - - - - - - - - -
        */
    }
}
