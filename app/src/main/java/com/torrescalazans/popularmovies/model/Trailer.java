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

package com.torrescalazans.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private final String mId; // "5a52578a0e0a260263022366"
    private final String mIso_639_1; // "en"
    private final String mIso_3166_1; // US
    private final String mKey; // xKJmEC5ieOk
    private final String mName; // "Official Trailer 1"
    private final String mSite; // "YouTube"
    private final int mSize; // 1080
    private final String mType; // "Trailer"

    public Trailer(String id, String iso_639_1, String iso_3166_1, String key, String name,
                   String site, int size, String type) {
        mId = id;
        mIso_639_1 = iso_639_1;
        mIso_3166_1 = iso_3166_1;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public String getIso_639_1() {
        return mIso_639_1;
    }

    public String getIso_3166_1() {
        return mIso_3166_1;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getSite() {
        return mSite;
    }

    public int getSize() {
        return mSize;
    }

    public String getType() {
        return mType;
    }

    protected Trailer(Parcel in) {
        mId = in.readString();
        mIso_639_1 = in.readString();
        mIso_3166_1 = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readInt();
        mType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mIso_639_1);
        dest.writeString(mIso_3166_1);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeInt(mSize);
        dest.writeString(mType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
