package com.torrescalazans.popularmovies.network.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.torrescalazans.popularmovies.network.parses.TheMovieDBJsonParser;

import java.io.IOException;
import java.net.URL;

/**
 * Created by jose torres on 10/22/17.
 */

public class NetworkHandler extends AsyncTask<NetworkManager.MoviesSortOrder, Void, String> {

    private static final String TAG = NetworkHandler.class.getSimpleName();

    private final NetworkEventListener mNetworkEventListener;

    public NetworkHandler(NetworkEventListener networkEventListener) {
        mNetworkEventListener = networkEventListener;
    }

    @Override
    protected String doInBackground(NetworkManager.MoviesSortOrder... params) {
        if (params.length == 0) {
            return null;
        }

        String theMovieDBResponse = null;

        try {
            NetworkManager.MoviesSortOrder sortOrder = params[0];
            URL TheMovieDBRequestUrl = NetworkManager.buildUrl(sortOrder);

            theMovieDBResponse = NetworkManager.getResponseFromUrl(TheMovieDBRequestUrl);
        } catch (IOException e) {
            Log.e(TAG, "Error downloading The Movie DB data", e);
            e.printStackTrace();
        }

        return theMovieDBResponse;
    }

    @Override
    protected void onPostExecute(String theMovieDBResponse) {
        super.onPostExecute(theMovieDBResponse);

        Log.d(TAG, "onPostExecute - theMovieDBResponse: " + theMovieDBResponse);

        if ((theMovieDBResponse != null) && (mNetworkEventListener != null)) {
            TheMovieDBJsonParser theMovieDBJsonParser = new TheMovieDBJsonParser(theMovieDBResponse);
            mNetworkEventListener.onUpdateData(theMovieDBJsonParser.parse());
        } else {
            mNetworkEventListener.onNetworkError();
        }
    }
}
