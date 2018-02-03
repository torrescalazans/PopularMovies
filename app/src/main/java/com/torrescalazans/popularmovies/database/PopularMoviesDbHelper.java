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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.torrescalazans.popularmovies.database.PopularMoviesContract.*;

/**
 * Manages a local database for movies data.
 */
public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    /*
    * This is the name of our database. Database names should be descriptive and end with the
    * .db extension.
    */
    public static final String DATABASE_NAME = "popularmovies.db";

    /*
    * If you change the database schema, you must increment the database version or the onUpgrade
    * method will not be called.
    */
    private static final int DATABASE_VERSION = 1;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a table to hold favorites data
        final String SQL_CREATE_FAVORITES_TABLE =

                "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +

                        FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_ID + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_TITLE + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL + " TEXT NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_ADULT + " INTEGER NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO + " INTEGER NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS + " TEXT," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY + " REAL NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE + " REAL NOT NULL," +
                        FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT + " INTEGER NOT NULL" +

                        "); ";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction. If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
