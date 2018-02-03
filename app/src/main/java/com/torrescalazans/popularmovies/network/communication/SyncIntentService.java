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

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.torrescalazans.popularmovies.network.parses.TheMovieDBJsonParser;

import java.io.IOException;
import java.net.URL;

public class SyncIntentService extends IntentService {

    private static final String TAG = SyncIntentService.class.getSimpleName();

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public SyncIntentService() {
        super(SyncIntentService.class.getName());
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     *               This may be null if the service is being restarted after
     *               its process has gone away; see
     *               {@link Service#onStartCommand}
     *               for details.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        NetworkManager.MoviesSortOrder sortOrder = (NetworkManager.MoviesSortOrder) intent.getSerializableExtra("sortOrder");
        final ResultReceiver resultReceiver = intent.getParcelableExtra("networkResultReceiver");

        String theMovieDBResponse = null;

        try {
            resultReceiver.send(STATUS_RUNNING, Bundle.EMPTY);

            URL TheMovieDBRequestUrl = NetworkManager.buildUrl(sortOrder);

            theMovieDBResponse = NetworkManager.getResponseFromUrl(TheMovieDBRequestUrl);

            if ((theMovieDBResponse != null) && (resultReceiver != null)) {
                TheMovieDBJsonParser theMovieDBJsonParser = new TheMovieDBJsonParser(theMovieDBResponse);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("moviesList", theMovieDBJsonParser.parse());

                resultReceiver.send(STATUS_FINISHED, bundle);
            } else {
                resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error downloading The Movie DB data", e);

            resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
        }
    }
}
