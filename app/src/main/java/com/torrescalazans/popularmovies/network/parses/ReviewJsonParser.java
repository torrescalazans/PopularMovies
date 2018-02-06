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

import com.torrescalazans.popularmovies.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewJsonParser {

    private final String TAG = ReviewJsonParser.class.getSimpleName();

    private final String THE_MOVIE_DB_KEY_RESULTS = "results";

    private final String REVIEW_ID = "id";
    private final String REVIEW_AUTHOR = "author";
    private final String REVIEW_CONTENT = "content";
    private final String REVIEW_URL = "url";

    private final String mReviewsData;
    private final ArrayList<Review> mReviewsList;

    public ReviewJsonParser(String reviewsData) {
        mReviewsData = reviewsData;
        mReviewsList = new ArrayList<>();
    }

    public ArrayList<Review> parse() {
        Log.d(TAG, "parse: " + mReviewsData);

        try {
            JSONObject reviewsDataJson = new JSONObject(mReviewsData);
            JSONArray reviewsJsonArray = reviewsDataJson.getJSONArray(THE_MOVIE_DB_KEY_RESULTS);

            for (int i = 0; i < reviewsJsonArray.length(); i++) {
                JSONObject jsonObject = reviewsJsonArray.getJSONObject(i);

                Review review = new Review(
                    jsonObject.getString(REVIEW_ID),
                    jsonObject.getString(REVIEW_AUTHOR),
                    jsonObject.getString(REVIEW_CONTENT),
                    jsonObject.getString(REVIEW_URL)
                );

                mReviewsList.add(review);
            }

            for (Review review: mReviewsList) {
                Log.d(TAG, review.toString());
            }

        } catch(JSONException e) {
            Log.e(TAG, "Error processing Reviews JSON data", e);
        }

        return mReviewsList;
    }
}
