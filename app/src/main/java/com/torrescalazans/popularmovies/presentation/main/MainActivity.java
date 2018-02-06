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
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;

import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.database.DatabaseUtils;
import com.torrescalazans.popularmovies.database.PopularMoviesContract;
import com.torrescalazans.popularmovies.databinding.ActivityMainBinding;
import com.torrescalazans.popularmovies.model.Movie;
import com.torrescalazans.popularmovies.network.communication.NetworkManager;
import com.torrescalazans.popularmovies.network.communication.NetworkResultReceiver;
import com.torrescalazans.popularmovies.presentation.moviedetails.MovieDetailsActivity;

import java.util.ArrayList;

import static com.torrescalazans.popularmovies.network.communication.NetworkManager.RequestType;
import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_ERROR;
import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_MOST_POPULAR_FINISHED;
import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_RUNNING;
import static com.torrescalazans.popularmovies.network.communication.NetworkRequestIntentService.STATUS_TOP_RATED_FINISHED;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        NetworkResultReceiver.Receiver,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int DISPLAY_MODE_MOST_POPULAR = 0;
    private static final int DISPLAY_MODE_TOP_RATED = 1;
    private static final int DISPLAY_MODE_FAVORITES = 2;

    private ActivityMainBinding mMainBinding;
    private MovieAdapter mMovieAdapter;

    private static final int ID_FAVORITES_LOADER = 0;

    private NetworkResultReceiver mNetworkResultReceiver;

    private int mCurrentDisplayMode;
    private Parcelable recyclerViewState;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final int columns = getResources().getInteger(R.integer.main_activity_columns);
        mGridLayoutManager = new GridLayoutManager(this, columns);
        mMainBinding.rvMoviesList.setLayoutManager(mGridLayoutManager);

        mMainBinding.rvMoviesList.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mMainBinding.rvMoviesList.setAdapter(mMovieAdapter);

        mNetworkResultReceiver = new NetworkResultReceiver(new Handler());
        mNetworkResultReceiver.setReceiver(this);

        if ((savedInstanceState != null)
                && (savedInstanceState.getSerializable("current_display_mode") != null)) {
            mCurrentDisplayMode = savedInstanceState.getInt("current_display_mode");

            switch (mCurrentDisplayMode) {
                case DISPLAY_MODE_MOST_POPULAR:
                    loadMostPopularData();
                    break;

                case DISPLAY_MODE_TOP_RATED:
                    loadTopRatedData();
                    break;

                case DISPLAY_MODE_FAVORITES:
                    loadFavoritesData();
                    break;
            }
        } else {
            loadMostPopularData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("current_display_mode", mCurrentDisplayMode);
        outState.putParcelable("grid_layout_manager_state", mGridLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if ((savedInstanceState != null)
                && (savedInstanceState.getParcelable("grid_layout_manager_state") != null)) {
            recyclerViewState = savedInstanceState.getParcelable("grid_layout_manager_state");
        }
    }

    private void loadMostPopularData() {
        setTitle(R.string.movie_discovery_activity_title_popular);

        NetworkManager.startSync(this, mNetworkResultReceiver,
                RequestType.MOST_POPULAR, 0);

        mCurrentDisplayMode = DISPLAY_MODE_MOST_POPULAR;
    }

    private void loadTopRatedData() {
        setTitle(R.string.movie_discovery_activity_title_top_rated);

        NetworkManager.startSync(this, mNetworkResultReceiver,
                RequestType.TOP_RATED, 0);

        mCurrentDisplayMode = DISPLAY_MODE_TOP_RATED;
    }

    private void loadFavoritesData() {
        setTitle(R.string.movie_discovery_activity_title_favorites);

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);

        mCurrentDisplayMode = DISPLAY_MODE_FAVORITES;
    }

    /**
     * This method will make the View for the movies data visible and hide the error message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showMoviesDataView() {

        if (recyclerViewState != null) {
            mGridLayoutManager.onRestoreInstanceState(recyclerViewState);
        }

        /* Then, hide the movies data */
        mMainBinding.tvConnectionErrorMessage.setVisibility(View.INVISIBLE);

        /* Then, hide the empty favorites list message */
        mMainBinding.tvEmptyFavoritesListMessage.setVisibility(View.INVISIBLE);

        /* Finally, make sure the movies data is visible */
        mMainBinding.rvMoviesList.setVisibility(View.VISIBLE);

        /* First, hide the loading indicator */
        mMainBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
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
        mMainBinding.rvMoviesList.setVisibility(View.INVISIBLE);

        /* Then, hide the movies data */
        mMainBinding.tvConnectionErrorMessage.setVisibility(View.INVISIBLE);

        /* Then, hide the empty favorites list message */
        mMainBinding.tvEmptyFavoritesListMessage.setVisibility(View.INVISIBLE);

        /* Finally, show the loading indicator */
        mMainBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showConnectionErrorMessage() {
        mMainBinding.tvEmptyFavoritesListMessage.setVisibility(View.INVISIBLE);

        mMainBinding.rvMoviesList.setVisibility(View.INVISIBLE);

        /* First, hide the loading indicator */
        mMainBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        mMainBinding.tvConnectionErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showEmptyFavoritesListMessage() {
        mMainBinding.rvMoviesList.setVisibility(View.INVISIBLE);

        /* First, hide the loading indicator */
        mMainBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        mMainBinding.tvConnectionErrorMessage.setVisibility(View.INVISIBLE);

        mMainBinding.tvEmptyFavoritesListMessage.setVisibility(View.VISIBLE);
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

            loadMostPopularData();
            return true;
        }

        if (id == R.id.movie_discovery_settings_action_top_rated) {
            Log.d(TAG, "onOptionsItemSelected - Top rated");

            loadTopRatedData();
            return true;
        }

        if (id == R.id.movie_discovery_settings_action_favorites) {
            Log.d(TAG, "onOptionsItemSelected - Favorites");

            loadFavoritesData();
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

            case STATUS_MOST_POPULAR_FINISHED:
            case STATUS_TOP_RATED_FINISHED:
                Log.d(TAG, "onReceiveResult - resultCode: " + resultCode);

                ArrayList<Movie> moviesList = resultData.getParcelableArrayList("movies_list");
                mMovieAdapter.updateData(null);
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
