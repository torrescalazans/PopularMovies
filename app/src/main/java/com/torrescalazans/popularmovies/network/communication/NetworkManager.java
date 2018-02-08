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

package com.torrescalazans.popularmovies.network.communication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.torrescalazans.popularmovies.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    private static final int CONNECTION_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 5000;

    private static final String THE_MOVIE_DB_STATIC_URL = "https://api.themoviedb.org/3";
    private static final String THE_MOVIE_DB_BASE_URL = THE_MOVIE_DB_STATIC_URL;
    private static final String THE_MOVIE_DB_API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

    private static final String PATH_MOVIES = "movie";
    private static final String PATH_MOST_POPULAR = "popular";
    private static final String PATH_TOP_RATED = "top_rated";
    private static final String PATH_VIDEOS = "videos";
    private static final String PATH_REVIEWS = "reviews";

    private static final String PARAM_THE_MOVIE_DB_API_KEY = "api_key";

    //https://api.themoviedb.org/3/movie/346364/videos?api_key=1a2684b35167e4b6e08e7fc38de473c6

    public enum RequestType {
        DEFAULT,
        MOST_POPULAR,
        TOP_RATED,
        VIDEOS,
        REVIEWS
    }

    // TODO How to pass the movie id until this level?
    public static URL buildUrl(RequestType requestType, int movieId) {
        String request = PATH_MOST_POPULAR;

        Uri builtUri = null;

        if (requestType == RequestType.DEFAULT
                || requestType == RequestType.MOST_POPULAR) {

            builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                    .appendEncodedPath(PATH_MOVIES)
                    .appendEncodedPath(PATH_MOST_POPULAR)
                    .appendQueryParameter(PARAM_THE_MOVIE_DB_API_KEY, THE_MOVIE_DB_API_KEY)
                    .build();

        } else if (requestType == RequestType.TOP_RATED) {
            builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                    .appendEncodedPath(PATH_MOVIES)
                    .appendEncodedPath(PATH_TOP_RATED)
                    .appendQueryParameter(PARAM_THE_MOVIE_DB_API_KEY, THE_MOVIE_DB_API_KEY)
                    .build();
        } else if (requestType == RequestType.VIDEOS) {
            builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                    .appendEncodedPath(PATH_MOVIES)
                    .appendEncodedPath(String.valueOf(movieId))
                    .appendEncodedPath(PATH_VIDEOS)
                    .appendQueryParameter(PARAM_THE_MOVIE_DB_API_KEY, THE_MOVIE_DB_API_KEY)
                    .build();
        } else if (requestType == RequestType.REVIEWS) {
            builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                    .appendEncodedPath(PATH_MOVIES)
                    .appendEncodedPath(String.valueOf(movieId))
                    .appendEncodedPath(PATH_REVIEWS)
                    .appendQueryParameter(PARAM_THE_MOVIE_DB_API_KEY, THE_MOVIE_DB_API_KEY)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     *
     * @return The contents of the HTTP response.
     *
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromUrl(URL url) throws IOException {
        Log.d(TAG, "getResponseFromUrl - url: " + url);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            StringBuffer stringBuffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            return stringBuffer.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    Log.e(TAG,"Error closing stream: ", e);
                }
            }
        }
    }

    /**
     * Helper method to perform a sync with The Movie DB data using an IntentService for
     * asynchronous execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startSync(@NonNull final Context context,
                                 @NonNull final NetworkResultReceiver networkResultReceiver,
                                 @NonNull final RequestType requestType,
                                 final int movieId) {

        Intent intentToSync = new Intent(context, NetworkRequestIntentService.class);

        intentToSync.putExtra("request_type", requestType);
        intentToSync.putExtra("network_result_receiver", networkResultReceiver);

        if (movieId != 0) {
            intentToSync.putExtra("movie_id", movieId);
        }

        context.startService(intentToSync);
    }
}
