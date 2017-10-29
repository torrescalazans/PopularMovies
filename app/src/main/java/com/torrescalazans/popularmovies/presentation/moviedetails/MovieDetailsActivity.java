package com.torrescalazans.popularmovies.presentation.moviedetails;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.model.Movie;

/**
 * Created by jose torres on 10/22/17.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private Movie mMovie;

    private TextView mMovieOriginalTitleTextView;
    private ImageView mMoviePosterThumbnail;
    private TextView mMovieSynopsisTextView;
    private TextView mMovieUserRating;
    private TextView mMovieReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovie = (Movie) getIntent().getSerializableExtra("serialize_data");

        mMovieOriginalTitleTextView = findViewById(R.id.tv_movie_original_title);
        mMoviePosterThumbnail = findViewById(R.id.iv_movie_poster_thumbnail);
        mMovieSynopsisTextView = findViewById(R.id.tv_movie_synopsis);
        mMovieUserRating = findViewById(R.id.tv_movie_user_rating);
        mMovieReleaseDate = findViewById(R.id.tv_movie_release_date);

        populateMovieDetails();
    }

    private void populateMovieDetails() {
        Log.d(TAG, "populateMovieDetails");

        mMovieOriginalTitleTextView.setText(mMovie.getOriginalTitle());

        Picasso.with(getApplicationContext()).load(mMovie.getPosterPathFullUrl())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mMoviePosterThumbnail);

        mMovieSynopsisTextView.setText(mMovie.getOverview());

        mMovieUserRating.setText(
                getResources().getString(
                        R.string.movie_details_activity_movie_user_rating,
                        mMovie.getVoteAverage()));

        mMovieReleaseDate.setText(
                getResources().getString(
                        R.string.movie_details_activity_movie_release_date,
                        mMovie.getReleaseDate()));
    }
}
