package com.torrescalazans.popularmovies.network.communication;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jose torres on 10/22/17.
 */

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    private static final String THE_MOVIE_DB_STATIC_URL = "https://api.themoviedb.org/3";
    private static final String THE_MOVIE_DB_BASE_URL = THE_MOVIE_DB_STATIC_URL;
    private static final String THE_MOVIE_DB_API_KEY = "yourTheMovieDbApiKey";

    private static final String PATH_MOST_POPULAR_MOVIES = "movie/popular";
    private static final String PATH_TOP_RATED_MOVIES = "movie/top_rated";

    private static final String PARAM_THE_MOVIE_DB_API_KEY = "api_key";

    public enum MoviesSortOrder {
        DEFAULT,
        MOST_POPULAR,
        TOP_RATED
    }

    public static URL buildUrl(MoviesSortOrder selectedSortOrder) {
        String sortOrder = PATH_MOST_POPULAR_MOVIES;

        if (selectedSortOrder == MoviesSortOrder.DEFAULT
                || selectedSortOrder == MoviesSortOrder.MOST_POPULAR) {
            sortOrder = PATH_MOST_POPULAR_MOVIES;
        } else if (selectedSortOrder == MoviesSortOrder.TOP_RATED) {
            sortOrder = PATH_TOP_RATED_MOVIES;
        }

        Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                .appendEncodedPath(sortOrder)
                .appendQueryParameter(PARAM_THE_MOVIE_DB_API_KEY, THE_MOVIE_DB_API_KEY)
                .build();

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
}
