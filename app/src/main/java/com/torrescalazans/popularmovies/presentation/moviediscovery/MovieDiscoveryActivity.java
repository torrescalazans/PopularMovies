package com.torrescalazans.popularmovies.presentation.moviediscovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.model.Movie;
import com.torrescalazans.popularmovies.network.communication.NetworkEventListener;
import com.torrescalazans.popularmovies.network.communication.NetworkHandler;
import com.torrescalazans.popularmovies.network.communication.NetworkManager;
import com.torrescalazans.popularmovies.presentation.moviedetails.MovieDetailsActivity;

import java.util.List;

/**
 * Created by jose torres on 10/22/17.
 */

public class MovieDiscoveryActivity extends AppCompatActivity implements NetworkEventListener<List<Movie>>, MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MovieDiscoveryActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mLoadingErrorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_discovery);

        mRecyclerView = findViewById(R.id.rv_movie_discovery);

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingErrorMessageTextView = findViewById(R.id.tv_loading_error_message);

        new NetworkHandler(this).execute(NetworkManager.MoviesSortOrder.MOST_POPULAR);
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
            Log.d(TAG, "onOptionsItemSelected - most popular");

            mMovieAdapter.setMoviesData(null);
            new NetworkHandler(this).execute(NetworkManager.MoviesSortOrder.MOST_POPULAR);

            setTitle(R.string.movie_discovery_activity_title_popular);

            return true;
        }

        if (id == R.id.movie_discovery_settings_action_top_rated) {
            Log.d(TAG, "onOptionsItemSelected - top rated");

            mMovieAdapter.setMoviesData(null);
            new NetworkHandler(this).execute(NetworkManager.MoviesSortOrder.TOP_RATED);

            setTitle(R.string.movie_discovery_activity_title_top_rated);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Log.d(TAG, "onClick: " + movie);

        Intent intentToStartDetailActivity = new Intent(this, MovieDetailsActivity.class);
        intentToStartDetailActivity.putExtra("serialize_data", movie);

        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void onUpdateData(List<Movie> moviesList) {
        Log.d(TAG, "updateData - moviesList: " + moviesList);

         if (mLoadingErrorMessageTextView.getVisibility() == View.VISIBLE) {
             mLoadingErrorMessageTextView.setVisibility(View.INVISIBLE);
             mRecyclerView.setVisibility(View.INVISIBLE);
         }

        mMovieAdapter.setMoviesData(moviesList);
    }

    @Override
    public void onNetworkError() {
        Log.d(TAG, "onNetworkError");

        if (mLoadingErrorMessageTextView.getVisibility() == View.INVISIBLE) {
            mLoadingErrorMessageTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }
}
