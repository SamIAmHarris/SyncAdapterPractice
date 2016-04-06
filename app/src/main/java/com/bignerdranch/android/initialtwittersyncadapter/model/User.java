package com.bignerdranch.android.initialtwittersyncadapter.model;

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
}
