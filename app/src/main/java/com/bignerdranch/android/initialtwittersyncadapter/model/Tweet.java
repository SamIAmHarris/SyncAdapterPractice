package com.bignerdranch.android.initialtwittersyncadapter.model;

public class Tweet {

    private String mServerId;
    private int mId;
    private String mText;
    private int mFavoriteCount;
    private int mRetweetCount;
    private User mUser;
    private String mUserId;

    public Tweet(String serverId, String text, int favoriteCount,
                 int retweetCount, User user) {
        mServerId = serverId;
        mText = text;
        mFavoriteCount = favoriteCount;
        mRetweetCount = retweetCount;
        mUser = user;
    }

    public Tweet(String serverId, String text, int favoriteCount,
                 int retweetCount, String userId) {
        mServerId = serverId;
        mText = text;
        mFavoriteCount = favoriteCount;
        mRetweetCount = retweetCount;
        mUserId = userId;
    }

    public String getText() {
        return mText;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getUserId() {
        return mUserId;
    }
}
