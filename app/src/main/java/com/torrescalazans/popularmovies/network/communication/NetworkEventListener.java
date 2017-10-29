package com.torrescalazans.popularmovies.network.communication;

/**
 * Created by jose torres on 10/22/17.
 */

public interface NetworkEventListener<T> {

    /**
     * Indicates that the listener needs to update its appearance or information based on
     * the result of the network response. Expected to be called from the main thread.
     */
    void onUpdateData(T networkResponse);

    /*
     * Handles network errors
     */
    void onNetworkError();
}
