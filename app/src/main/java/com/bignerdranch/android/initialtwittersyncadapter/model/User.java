package com.bignerdranch.android.initialtwittersyncadapter.model;

import android.content.ContentValues;

import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.DatabaseContract;

public class User {

    private int mId;
    private String mServerId;
    private String mScreenName;
    private String mPhotoUrl;

    public User(String serverId, String screenName, String photoUrl) {
        mServerId = serverId;
        mScreenName = screenName;
        mPhotoUrl = photoUrl;
    }

    public String getServerId() {
        return mServerId;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.User.SERVER_ID, mServerId);
        cv.put(DatabaseContract.User.SCREEN_NAME, mScreenName);
        cv.put(DatabaseContract.User.PHOTO_URL, mPhotoUrl);
        return cv;
    }
}
