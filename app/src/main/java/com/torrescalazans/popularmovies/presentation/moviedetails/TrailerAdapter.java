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
import com.torrescalazans.popularmovies.model.Trailer;

import java.util.ArrayList;

/**
 * {@link TrailerAdapter} exposes a list of trailers
 * from a {@link Cursor} to a {@link RecyclerView}.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    /* An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final TrailerAdapterOnClickHandler mOnClickHandler;

    private ArrayList<Trailer> mTrailersArrayList;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    /**
     * Creates a TrailerAdapter.
     *
     * @param onClickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailerAdapter(TrailerAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // TODO how to use the Binding at this point? There's no activity here.
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.trailer_list_item, parent, false);

        view.setFocusable(true);

        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {

        Trailer trailer = mTrailersArrayList.get(position);

        holder.mName.setText(trailer.getName());
        holder.mSite.setText(trailer.getSite());
    }

    @Override
    public int getItemCount() {
        return (mTrailersArrayList == null? 0 : mTrailersArrayList.size());
    }

    /**
     * Cache of the children views for a trailer list item.
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mName;
        public final TextView mSite;

        public TrailerAdapterViewHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.tv_trailer_name);
            mSite = view.findViewById(R.id.tv_trailer_site);

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
            Trailer trailer = mTrailersArrayList.get(adapterPosition);
            mOnClickHandler.onClick(trailer);
        }
    }

    public void updateData(ArrayList<Trailer> trailersArrayList) {
        mTrailersArrayList = trailersArrayList;
        notifyDataSetChanged();
    }
}
