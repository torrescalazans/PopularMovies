package com.torrescalazans.popularmovies.presentation.moviediscovery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by jose torres on 10/24/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    /*
 * An on-click handler that we've defined to make it easy for an Activity to interface with
 * our RecyclerView
 */
    private final MovieAdapterOnClickHandler mOnClickHandler;

    private List<Movie> mMoviesList;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param onClickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = view.findViewById(R.id.iv_movie_item);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMoviesList.get(adapterPosition);
            mOnClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        Context context = movieAdapterViewHolder.mMovieImageView.getContext();
        Movie movie = mMoviesList.get(position);

        Picasso.with(context).load(movie.getPosterPathFullUrl())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(movieAdapterViewHolder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        return (null == mMoviesList ? 0 : mMoviesList.size());
    }

    public void setMoviesData(List<Movie> moviesList) {
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }
}
