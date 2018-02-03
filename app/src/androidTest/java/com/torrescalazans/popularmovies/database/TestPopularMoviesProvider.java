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

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestPopularMoviesProvider {

    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete all entries in the tasks directory to do so.
     */
    @Before
    public void setUp() {
        /* Use TaskDbHelper to get access to a writable database */
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(PopularMoviesContract.FavoriteEntry.TABLE_NAME, null, null);
    }

    //================================================================================
    // Test ContentProvider Registration
    //================================================================================

    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     */
    @Test
    public void testProviderRegistry() {

        /*
         * A ComponentName is an identifier for a specific application component, such as an
         * Activity, ContentProvider, BroadcastReceiver, or a Service.
         *
         * Two pieces of information are required to identify a component: the package (a String)
         * it exists in, and the class (a String) name inside of that package.
         *
         * We will use the ComponentName for our ContentProvider class to ask the system
         * information about the ContentProvider, specifically, the authority under which it is
         * registered.
         */
        String packageName = mContext.getPackageName();
        String taskProviderClassName = PopularMoviesProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, taskProviderClassName);

        try {

            /*
             * Get a reference to the package manager. The package manager allows us to access
             * information about packages installed on a particular device. In this case, we're
             * going to use it to get some information about our ContentProvider under test.
             */
            PackageManager pm = mContext.getPackageManager();

            /* The ProviderInfo will contain the authority, which is what we want to test */
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: PopularMoviesProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: PopularMoviesProvider not registered at " + mContext.getPackageName();
            /*
             * This exception is thrown if the ContentProvider hasn't been registered with the
             * manifest at all. If this is the case, you need to double check your
             * AndroidManifest file
             */
            fail(providerNotRegisteredAtAll);
        }
    }

    //================================================================================
    // Test UriMatcher
    //================================================================================

    private static final Uri TEST_FAVORITES = PopularMoviesContract.FavoriteEntry.CONTENT_URI;

    // Content URI for a single favorite with id = 1
    private static final Uri TEST_FAVORITE_WITH_ID = TEST_FAVORITES.buildUpon().appendPath("1").build();

    /**
     * This function tests that the UriMatcher returns the correct integer value for
     * each of the Uri types that the ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    @Test
    public void testUriMatcher() {

        /* Create a URI matcher that the PopularMoviesProvider uses */
        UriMatcher testMatcher = PopularMoviesProvider.buildUriMatcher();

        /* Test that the code returned from our matcher matches the expected FAVORITES int */
        String tasksUriDoesNotMatch = "Error: The FAVORITES URI was matched incorrectly.";
        int actualTasksMatchCode = testMatcher.match(TEST_FAVORITES);
        int expectedTasksMatchCode = PopularMoviesProvider.FAVORITES;
        assertEquals(tasksUriDoesNotMatch,
                actualTasksMatchCode,
                expectedTasksMatchCode);

        /* Test that the code returned from our matcher matches the expected TEST_FAVORITE_WITH_ID */
        String taskWithIdDoesNotMatch =
                "Error: The TEST_FAVORITE_WITH_ID URI was matched incorrectly.";
        int actualTaskWithIdCode = testMatcher.match(TEST_FAVORITE_WITH_ID);
        int expectedTaskWithIdCode = PopularMoviesProvider.FAVORITE_WITH_ID;
        assertEquals(taskWithIdDoesNotMatch,
                actualTaskWithIdCode,
                expectedTaskWithIdCode);
    }

    //================================================================================
    // Test Insert
    //================================================================================

    /**
     * Tests inserting a single row of data via a ContentResolver
     */
    @Test
    public void testInsert() {

        /* Create values to insert */
        ContentValues testFavoriteValues = new ContentValues();
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID, "346364");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW, "In a small town in Maine, seven children known as The Losers Club come face to face with life problems, bullies and a monster that takes the shape of a clown called Pennywise.");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE, "2017-09-05");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE, "en");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH, "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH, "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL, "https://image.tmdb.org/t/p/w185/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ADULT, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS, "18,27,53");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY, 485.574679);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE, 7.1);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT, 6124);

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver favoriteObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                PopularMoviesContract.FavoriteEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                favoriteObserver);

        Uri uri = contentResolver.insert(PopularMoviesContract.FavoriteEntry.CONTENT_URI, testFavoriteValues);

        // Get the favorite ID from the URI path
        long id = ContentUris.parseId(uri);

        Uri expectedUri = ContentUris.withAppendedId(PopularMoviesContract.FavoriteEntry.CONTENT_URI, id);

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);

        /*
         * If this fails, it's likely you didn't call notifyChange in your insert method from
         * your ContentProvider.
         */
        favoriteObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(favoriteObserver);
    }

    //================================================================================
    // Test Query (for favorites directory)
    //================================================================================

    /**
     * Inserts data, then tests if a query for the tasks directory returns that data as a Cursor
     */
    @Test
    public void testQueryAllItems() {

        /* Get access to a writable database */
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Create values to insert */
        ContentValues testFavoriteValues = new ContentValues();
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID, "346364");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW, "In a small town in Maine, seven children known as The Losers Club come face to face with life problems, bullies and a monster that takes the shape of a clown called Pennywise.");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE, "2017-09-05");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE, "en");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH, "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH, "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL, "https://image.tmdb.org/t/p/w185/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ADULT, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS, "18,27,53");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY, 485.574679);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE, 7.1);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT, 6124);

        /* Insert ContentValues into database and get a row ID back */
        long favoriteRowId = database.insert(
                /* Table to insert values into */
                PopularMoviesContract.FavoriteEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testFavoriteValues);

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, favoriteRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        /* Perform the ContentProvider query */
        Cursor favoriteCursor = mContext.getContentResolver().query(
                PopularMoviesContract.FavoriteEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, favoriteCursor != null);

        /* We are done with the cursor, close it now. */
        favoriteCursor.close();
    }

    //================================================================================
    // Test query (for a single item)
    //================================================================================

    /**
     * Inserts data, then tests if a query for a single item returns that data as a Cursor
     */
    @Test
    public void testQuerySingleItem() {

        /* Get access to a writable database */
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Create values to insert */
        ContentValues testFavoriteValues = new ContentValues();
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID, "123456");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW, "In a small town in Maine, seven children known as The Losers Club come face to face with life problems, bullies and a monster that takes the shape of a clown called Pennywise.");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE, "2017-09-05");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE, "en");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH, "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH, "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL, "https://image.tmdb.org/t/p/w185/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ADULT, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS, "18,27,53");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY, 485.574679);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE, 7.1);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT, 6124);

        /* Insert ContentValues into database and get a row ID back */
        long favoriteRowId = database.insert(
                /* Table to insert values into */
                PopularMoviesContract.FavoriteEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testFavoriteValues);

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, favoriteRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        String movieId = testFavoriteValues.getAsString(
                PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID);

        String[] selectionArguments = new String[]{movieId};

        /* Perform the ContentProvider query */
        Cursor favoriteCursor = mContext.getContentResolver().query(
                PopularMoviesContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(movieId).build(),
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID,
                /* Values for "where" clause */
                selectionArguments,
                /* Sort order to return in Cursor */
                null);

        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, favoriteCursor != null);

        favoriteCursor.moveToFirst();

        final int movieIdIndex = favoriteCursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID);
        String retrievedMovieId = favoriteCursor.getString(movieIdIndex);

        String querySingleItemFailed = "Query failed to return the inserted single item";
        assertTrue(querySingleItemFailed, retrievedMovieId != movieId);

        /* We are done with the cursor, close it now. */
        favoriteCursor.close();
    }

    //================================================================================
    // Test Delete (for a single item)
    //================================================================================

    /**
     * Tests deleting a single row of data via a ContentResolver
     */
    @Test
    public void testDelete() {

        /* Access writable database */
        PopularMoviesDbHelper helper = new PopularMoviesDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* Create a new row of favorites data */
        ContentValues testFavoriteValues = new ContentValues();
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID, "346364");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_TITLE, "It");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_OVERVIEW, "In a small town in Maine, seven children known as The Losers Club come face to face with life problems, bullies and a monster that takes the shape of a clown called Pennywise.");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_RELEASE_DATE, "2017-09-05");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ORIGINAL_LANGUAGE, "en");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_PATH, "/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_BACKDROP_PATH, "/tcheoA2nPATCm2vvXw2hVQoaEFD.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POSTER_URL, "https://image.tmdb.org/t/p/w185/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ADULT, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VIDEO, 0);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_GENRE_IDS, "18,27,53");
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_POPULARITY, 485.574679);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_AVERAGE, 7.1);
        testFavoriteValues.put(PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_VOTE_COUNT, 6124);

        /* Insert ContentValues into database and get a row ID back */
        long favoriteRowId = database.insert(
                /* Table to insert values into */
                PopularMoviesContract.FavoriteEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testFavoriteValues);

        /* Always close the database when you're through with it */
        database.close();

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, favoriteRowId != -1);

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver favoriteObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                PopularMoviesContract.FavoriteEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                favoriteObserver);

        /* The delete method deletes the previously inserted row with movie id = 346364 */
        Uri uriToDelete = PopularMoviesContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath("346364").build();
        int favoritesDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, favoritesDeleted != 0);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        favoriteObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(favoriteObserver);
    }
}
