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

import com.torrescalazans.popularmovies.network.parses.ReviewJsonParser;
import com.torrescalazans.popularmovies.network.parses.TheMovieDBJsonParser;
import com.torrescalazans.popularmovies.network.parses.TrailerJsonParser;

import java.io.IOException;
import java.net.URL;

public class NetworkRequestIntentService extends IntentService {

    private static final String TAG = NetworkRequestIntentService.class.getSimpleName();

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_MOST_POPULAR_FINISHED = 1;
    public static final int STATUS_TOP_RATED_FINISHED = 2;
    public static final int STATUS_TRAILERS_FINISHED = 3;
    public static final int STATUS_REVIEWS_FINISHED = 4;
    public static final int STATUS_ERROR = 5;

    public NetworkRequestIntentService() {
        super(NetworkRequestIntentService.class.getName());
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

        final NetworkManager.RequestType requestType;
        if ((intent != null) && (intent.hasExtra("request_type"))) {
            requestType = (NetworkManager.RequestType) intent.getSerializableExtra("request_type");
        } else {
            throw new RuntimeException("Missing intent extra: request_type");
        }

        final ResultReceiver resultReceiver;
        if ((intent != null) && (intent.hasExtra("network_result_receiver"))) {
            resultReceiver = intent.getParcelableExtra("network_result_receiver");
        } else {
            throw new RuntimeException("Missing intent extra: network_result_receiver");
        }

        final int movieId;
        if ((intent != null) && (intent.hasExtra("movie_id"))) {
            movieId = intent.getIntExtra("movie_id", 0);
        } else {
            movieId = 0;
        }

        String theMovieDBResponse = null;

        try {
            resultReceiver.send(STATUS_RUNNING, Bundle.EMPTY);

            URL TheMovieDBRequestUrl = NetworkManager.buildUrl(requestType, movieId);

            theMovieDBResponse = NetworkManager.getResponseFromUrl(TheMovieDBRequestUrl);

            // TODO What's the best way to avoid logic to create different responses at this level?
            if (requestType == NetworkManager.RequestType.MOST_POPULAR) {
                if ((theMovieDBResponse != null) && (resultReceiver != null)) {
                    TheMovieDBJsonParser theMovieDBJsonParser = new TheMovieDBJsonParser(theMovieDBResponse);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("movies_list", theMovieDBJsonParser.parse());

                    resultReceiver.send(STATUS_MOST_POPULAR_FINISHED, bundle);
                } else {
                    resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
                }
            } else if (requestType == NetworkManager.RequestType.TOP_RATED) {
                    if ((theMovieDBResponse != null) && (resultReceiver != null)) {
                        TheMovieDBJsonParser theMovieDBJsonParser = new TheMovieDBJsonParser(theMovieDBResponse);

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("movies_list", theMovieDBJsonParser.parse());

                        resultReceiver.send(STATUS_TOP_RATED_FINISHED, bundle);
                    } else {
                        resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
                    }
            } else if (requestType == NetworkManager.RequestType.VIDEOS) {
                if ((theMovieDBResponse != null) && (resultReceiver != null)) {
                    TrailerJsonParser trailerJsonParser = new TrailerJsonParser(theMovieDBResponse);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("trailers_list", trailerJsonParser.parse());

                    resultReceiver.send(STATUS_TRAILERS_FINISHED, bundle);
                } else {
                    resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
                }
            } else if (requestType == NetworkManager.RequestType.REVIEWS) {
                if ((theMovieDBResponse != null) && (resultReceiver != null)) {
                    ReviewJsonParser reviewJsonParser = new ReviewJsonParser(theMovieDBResponse);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("reviews_list", reviewJsonParser.parse());

                    resultReceiver.send(STATUS_REVIEWS_FINISHED, bundle);
                } else {
                    resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error downloading The Movie DB data", e);

            resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
        }
    }
}
