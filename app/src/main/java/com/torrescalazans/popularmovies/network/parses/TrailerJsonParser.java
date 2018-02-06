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

package com.torrescalazans.popularmovies.network.parses;

import android.util.Log;

import com.torrescalazans.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrailerJsonParser {

    private final String TAG = TrailerJsonParser.class.getSimpleName();

    private final String THE_MOVIE_DB_KEY_RESULTS = "results";

    private final String TRAILER_ID = "id";
    private final String TRAILER_ISO_639_1 = "iso_639_1";
    private final String TRAILER_ISO_3166_1 = "iso_3166_1";
    private final String TRAILER_KEY = "key";
    private final String TRAILER_NAME = "name";
    private final String TRAILER_SITE = "site";
    private final String TRAILER_SIZE = "size";
    private final String TRAILER_TYPE = "type";

    private final String mTrailersData;
    private final ArrayList<Trailer> mTrailersList;

    public TrailerJsonParser(String trailersData) {
        mTrailersData = trailersData;
        mTrailersList = new ArrayList<>();
    }

    public ArrayList<Trailer> parse() {
        Log.d(TAG, "parse: " + mTrailersData);

        try {
            JSONObject trailersDataJson = new JSONObject(mTrailersData);
            JSONArray trailersJsonArray = trailersDataJson.getJSONArray(THE_MOVIE_DB_KEY_RESULTS);

            for (int i = 0; i < trailersJsonArray.length(); i++) {
                JSONObject jsonObject = trailersJsonArray.getJSONObject(i);

                Trailer trailer = new Trailer(
                    jsonObject.getString(TRAILER_ID),
                    jsonObject.getString(TRAILER_ISO_639_1),
                    jsonObject.getString(TRAILER_ISO_3166_1),
                    jsonObject.getString(TRAILER_KEY),
                    jsonObject.getString(TRAILER_NAME),
                    jsonObject.getString(TRAILER_SITE),
                    jsonObject.getInt(TRAILER_SIZE),
                    jsonObject.getString(TRAILER_TYPE)
                );

                mTrailersList.add(trailer);
            }

            for (Trailer trailer: mTrailersList) {
                Log.d(TAG, trailer.toString());
            }

        } catch(JSONException e) {
            Log.e(TAG, "Error processing Trailers JSON data", e);
        }

        return mTrailersList;
    }
}
