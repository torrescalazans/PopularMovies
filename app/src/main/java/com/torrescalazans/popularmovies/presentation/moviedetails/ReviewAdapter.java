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

package com.torrescalazans.popularmovies.presentation.moviedetails;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.torrescalazans.popularmovies.R;
import com.torrescalazans.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * {@link ReviewAdapter} exposes a list of reviews
 * from a {@link Cursor} to a {@link RecyclerView}.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    /* An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private ArrayList<Review> mReviewsArrayList;

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // TODO how to use the Binding at this point? There's no activity here.
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.review_list_item, parent, false);

        view.setFocusable(true);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewsArrayList.get(position);

        holder.mAuthor.setText(review.getAuthor());
        holder.mContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return (mReviewsArrayList == null? 0 : mReviewsArrayList.size());
    }

    /**
     * Cache of the children views for a review list item.
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mAuthor;
        public final TextView mContent;

        public ReviewAdapterViewHolder(View view) {
            super(view);

            mAuthor = view.findViewById(R.id.tv_author);
            mContent = view.findViewById(R.id.tv_content);
        }
    }

    public void updateData(ArrayList<Review> reviewsArrayList) {
        mReviewsArrayList = reviewsArrayList;
        notifyDataSetChanged();
    }
}
