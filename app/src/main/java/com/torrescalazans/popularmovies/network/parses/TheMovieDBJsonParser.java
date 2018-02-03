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

import com.torrescalazans.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TheMovieDBJsonParser {

    private final String TAG = TheMovieDBJsonParser.class.getSimpleName();

    private static final String THE_MOVIE_DB_STATIC_IMAGE_URL = "https://image.tmdb.org/t/p/";
    private static final String THE_MOVIE_DB_IMAGE_BASE_URL = THE_MOVIE_DB_STATIC_IMAGE_URL;
    private static final String THE_MOVIE_DB_IMAGE_FILE_SIZE_W185 = "w185";

    private final String THE_MOVIE_DB_KEY_PAGE = "page";
    private final String THE_MOVIE_DB_KEY_TOTAL_RESULTS = "total_results";
    private final String THE_MOVIE_DB_KEY_TOTAL_PAGES = "total_pages";
    private final String THE_MOVIE_DB_KEY_RESULTS = "results";

    private final String MOVIE_ID = "id";
    private final String MOVIE_TITLE = "title";
    private final String MOVIE_ORIGINAL_TITLE = "original_title";
    private final String MOVIE_OVERVIEW = "overview";
    private final String MOVIE_RELEASE_DATE = "release_date";
    private final String MOVIE_ORIGINAL_LANGUAGE = "original_language";
    private final String MOVIE_POSTER_PATH = "poster_path";
    private final String MOVIE_BACKDROP_PATH = "backdrop_path";
    private final String MOVIE_ADULT = "adult";
    private final String MOVIE_VIDEO = "video";
    private final String MOVIE_GENRE_IDS = "genre_ids";
    private final String MOVIE_POPULARITY = "popularity";
    private final String MOVIE_VOTE_AVERAGE = "vote_average";
    private final String MOVIE_VOTE_COUNT = "vote_count";

    private final String mTheMovieDBData;
    private final ArrayList<Movie> mMoviesList;

    public TheMovieDBJsonParser(String theMovieDBData) {
        mTheMovieDBData = theMovieDBData;
        mMoviesList = new ArrayList<>();
    }

    public ArrayList<Movie> parse() {
        Log.d(TAG, "parse: " + mTheMovieDBData);

        try {
            JSONObject theMovieDBDataJson = new JSONObject(mTheMovieDBData);
            JSONArray moviesJsonArray = theMovieDBDataJson.getJSONArray(THE_MOVIE_DB_KEY_RESULTS);

            for (int i = 0; i < moviesJsonArray.length(); i++) {
                JSONObject jsonObject = moviesJsonArray.getJSONObject(i);

                Movie movie = new Movie(
                    jsonObject.getString(MOVIE_ID),
                    jsonObject.getString(MOVIE_TITLE),
                    jsonObject.getString(MOVIE_ORIGINAL_TITLE),
                    jsonObject.getString(MOVIE_OVERVIEW),
                    jsonObject.getString(MOVIE_RELEASE_DATE),
                    jsonObject.getString(MOVIE_ORIGINAL_LANGUAGE),
                    jsonObject.getString(MOVIE_POSTER_PATH),
                    populatePosterUrl(jsonObject.getString(MOVIE_POSTER_PATH)),
                    jsonObject.getString(MOVIE_BACKDROP_PATH),
                    jsonObject.getBoolean(MOVIE_ADULT),
                    jsonObject.getBoolean(MOVIE_VIDEO),
                    populateGenreIds(jsonObject.getJSONArray(MOVIE_GENRE_IDS)),
                    jsonObject.getDouble(MOVIE_POPULARITY),
                    jsonObject.getDouble(MOVIE_VOTE_AVERAGE),
                    jsonObject.getInt(MOVIE_VOTE_COUNT)
                );

                populatePosterUrl(movie.getPosterPath());
                mMoviesList.add(movie);
            }

            for (Movie movie: mMoviesList) {
                Log.d(TAG, movie.toString());
            }

        } catch(JSONException e) {
            Log.e(TAG, "Error processing The Movie DB JSON data", e);
        }

        return mMoviesList;
    }

    private String populatePosterUrl(String posterUrl) {
        StringBuilder posterUrlStringBuilder = new StringBuilder();

        posterUrlStringBuilder.append(THE_MOVIE_DB_IMAGE_BASE_URL)
                         .append(THE_MOVIE_DB_IMAGE_FILE_SIZE_W185)
                         .append(posterUrl);

        return posterUrlStringBuilder.toString();
    }

    private int[] populateGenreIds(JSONArray jsonArray) {
        int length = jsonArray.length();
        int[] genreIds = new int[length];

        try {
            for (int i = 0; i < length; i++) {
                genreIds[i] = jsonArray.getInt(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return genreIds;
    }
}
