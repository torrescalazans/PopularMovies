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

package com.torrescalazans.popularmovies.presentation.main;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.database.DatabaseUtils;
import com.torrescalazans.popularmovies.database.PopularMoviesContract;
import com.torrescalazans.popularmovies.model.Movie;
import com.torrescalazans.popularmovies.network.communication.NetworkManager;
import com.torrescalazans.popularmovies.network.communication.NetworkResultReceiver;
import com.torrescalazans.popularmovies.presentation.moviedetails.MovieDetailsActivity;

import java.util.ArrayList;

import static com.torrescalazans.popularmovies.network.communication.NetworkManager.MoviesSortOrder;
import static com.torrescalazans.popularmovies.network.communication.SyncIntentService.STATUS_ERROR;
import static com.torrescalazans.popularmovies.network.communication.SyncIntentService.STATUS_FINISHED;
import static com.torrescalazans.popularmovies.network.communication.SyncIntentService.STATUS_RUNNING;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        NetworkResultReceiver.Receiver,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mLoadingIndicator;
    private TextView mConnectionErrorMessageTextView;
    private TextView mEmptyFavoritesListMessageTextView;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private static final int ID_FAVORITES_LOADER = 0;

    NetworkResultReceiver mNetworkResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_discovery);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mConnectionErrorMessageTextView = findViewById(R.id.tv_connection_error_message);
        mEmptyFavoritesListMessageTextView = (TextView) findViewById(R.id.tv_empty_favorites_list_message);

        mRecyclerView = findViewById(R.id.rv_movie_discovery);

        final int columns = getResources().getInteger(R.integer.movie_discovery_activity_gallery_columns);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mNetworkResultReceiver = new NetworkResultReceiver(new Handler());
        mNetworkResultReceiver.setReceiver(this);

        NetworkManager.startSync(this, mNetworkResultReceiver, MoviesSortOrder.MOST_POPULAR);
    }

    /**
     * This method will make the View for the movies data visible and hide the error message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showMoviesDataView() {

        /* Then, hide the movies data */
        mConnectionErrorMessageTextView.setVisibility(View.INVISIBLE);

        /* Then, hide the empty favorites list message */
        mEmptyFavoritesListMessageTextView.setVisibility(View.INVISIBLE);

        /* Finally, make sure the movies data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);

        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the movies View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoadingIndicator() {

        /* Then, hide the movies data */
        mRecyclerView.setVisibility(View.INVISIBLE);

        /* Then, hide the movies data */
        mConnectionErrorMessageTextView.setVisibility(View.INVISIBLE);

        /* Then, hide the empty favorites list message */
        mEmptyFavoritesListMessageTextView.setVisibility(View.INVISIBLE);

        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showConnectionErrorMessage() {
        mEmptyFavoritesListMessageTextView.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.INVISIBLE);

        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mConnectionErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showEmptyFavoritesListMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);

        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mConnectionErrorMessageTextView.setVisibility(View.INVISIBLE);

        mEmptyFavoritesListMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_movie_discovery_settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        int id = item.getItemId();

        if (id == R.id.movie_discovery_settings_action_popular) {
            Log.d(TAG, "onOptionsItemSelected - Most popular");

            setTitle(R.string.movie_discovery_activity_title_popular);

            mMovieAdapter.updateData(null);
            NetworkManager.startSync(this, mNetworkResultReceiver, MoviesSortOrder.MOST_POPULAR);

            return true;
        }

        if (id == R.id.movie_discovery_settings_action_top_rated) {
            Log.d(TAG, "onOptionsItemSelected - Top rated");

            setTitle(R.string.movie_discovery_activity_title_top_rated);

            mMovieAdapter.updateData(null);
            NetworkManager.startSync(this, mNetworkResultReceiver, MoviesSortOrder.TOP_RATED);

            return true;
        }

        if (id == R.id.movie_discovery_settings_action_favorites) {
            Log.d(TAG, "onOptionsItemSelected - Favorites");

            setTitle(R.string.movie_discovery_activity_title_favorites);

            /*
             Ensure a loader is initialized and active. If the loader doesn't already exist, one is
             created, otherwise the last created loader is re-used.
             */
            getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Log.d(TAG, "onClick: " + movie);

        Intent intentToStartDetailActivity = new Intent(this, MovieDetailsActivity.class);
        intentToStartDetailActivity.putExtra("movie_data", movie);

        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "onReceiveResult - resultCode: " + resultCode);

        switch (resultCode) {
            case STATUS_RUNNING:
                Log.d(TAG, "onReceiveResult - resultCode: STATUS_RUNNING");

                showLoadingIndicator();
                break;

            case STATUS_FINISHED:
                Log.d(TAG, "onReceiveResult - resultCode: STATUS_FINISHED");

                showMoviesDataView();

                ArrayList<Movie> moviesList = resultData.getParcelableArrayList("moviesList");
                mMovieAdapter.updateData(moviesList);

                showMoviesDataView();
                break;

            case STATUS_ERROR:
                Log.d(TAG, "onReceiveResult - resultCode: STATUS_ERROR");

                showConnectionErrorMessage();
                break;
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param loaderId The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        showLoadingIndicator();

        switch (loaderId) {

            case ID_FAVORITES_LOADER:

                /* URI for all rows of favorites data in our favorites table */
                Uri forecastQueryUri = PopularMoviesContract.FavoriteEntry.CONTENT_URI;

                /* Sort order: Ascending by title */
                String sortOrder = PopularMoviesContract.FavoriteEntry.COLUMN_NAME_MOVIE_TITLE + " ASC";

                return new CursorLoader(this,
                        PopularMoviesContract.FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter #CursorAdapter(Context, * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: " + data);

        ArrayList<Movie> moviesList = DatabaseUtils.arrayListFromCursor(data);

        if (!moviesList.isEmpty()) {
            mMovieAdapter.updateData(null);
            mMovieAdapter.updateData(moviesList);

            showMoviesDataView();
        } else {
            showEmptyFavoritesListMessage();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.updateData(null);
    }
}
