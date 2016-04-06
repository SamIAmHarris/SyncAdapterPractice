package com.bignerdranch.android.initialtwittersyncadapter.controller;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.initialtwittersyncadapter.R;
import com.bignerdranch.android.initialtwittersyncadapter.account.Authenticator;
import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.DatabaseContract;
import com.bignerdranch.android.initialtwittersyncadapter.model.Tweet;
import com.bignerdranch.android.initialtwittersyncadapter.model.User;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TweetListFragment extends Fragment {

    private static final String TAG = "TweetListFragment";

    private String accessToken;
    private Account account;
    private RecyclerView recyclerView;
    private TweetAdapter tweetAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        recyclerView = (RecyclerView)
                view.findViewById(R.id.fragment_tweet_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tweetAdapter = new TweetAdapter(new ArrayList<Tweet>());
        recyclerView.setAdapter(tweetAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        clearDb();
        testInsert();
        testQuery();
    }

    private void testInsert() {
        User user = new User("server_id", "redhornet5490", "cool_guys.png");
        Tweet tweet = new Tweet("server_id", "anti tweet", 0, 0, user);

        Uri userUri = getActivity().getContentResolver()
                .insert(DatabaseContract.User.CONTENT_URI, user.getContentValues());
        Log.d(TAG, "Inserted user into uri: " + userUri);

        Uri tweetUri = getActivity().getContentResolver()
                .insert(DatabaseContract.Tweet.CONTENT_URI, tweet.getContentValues());
        Log.d(TAG, "Inserted tweet into uri: " + tweetUri);
    }

    private void clearDb() {
        getActivity().getContentResolver()
                .delete(DatabaseContract.User.CONTENT_URI, null, null);
        getActivity().getContentResolver()
                .delete(DatabaseContract.Tweet.CONTENT_URI, null, null);
    }

    private void testQuery() {
        Cursor userCursor = getActivity().getContentResolver()
                .query(DatabaseContract.User.CONTENT_URI, null, null, null, null);
        Log.d(TAG, "Have user cursor: " + userCursor);
        userCursor.close();
        Cursor tweetCursor = getActivity().getContentResolver()
                .query(DatabaseContract.Tweet.CONTENT_URI, null, null, null, null);
        Log.d(TAG, "Have user cursor: " + tweetCursor);
        tweetCursor.close();
    }



    @Override
    public void onStart() {
        super.onStart();
        fetchAccessToken();
    }

    private void fetchAccessToken() {
        AccountManager accountManager = AccountManager.get(getActivity());
        account = new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        accountManager.getAuthToken(account, Authenticator.AUTH_TOKEN_TYPE, null, getActivity(),
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            accessToken = bundle.getString(
                                    AccountManager.KEY_AUTHTOKEN);
                            Log.d(TAG, "Have access token: " + accessToken);
                        } catch (AuthenticatorException |
                                OperationCanceledException |
                                IOException e) {
                            Log.e(TAG, "Got an exception", e);
                        }
                    }
                }, null);
    }

    private class TweetAdapter extends RecyclerView.Adapter<TweetHolder> {
        private List<Tweet> mTweetList;

        public TweetAdapter(List<Tweet> tweetList) {
            mTweetList = tweetList;
        }

        public void setTweetList(List<Tweet> tweetList) {
            mTweetList = tweetList;
            notifyDataSetChanged();
        }

        @Override
        public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_tweet, parent, false);
            return new TweetHolder(view);
        }

        @Override
        public void onBindViewHolder(TweetHolder holder, int position) {
            Tweet tweet = mTweetList.get(position);
            holder.bindTweet(tweet);
        }

        @Override
        public int getItemCount() {
            return mTweetList.size();
        }
    }

    private class TweetHolder extends RecyclerView.ViewHolder {
        private ImageView mProfileImageView;
        private TextView mTweetTextView;
        private TextView mScreenNameTextView;

        public TweetHolder(View itemView) {
            super(itemView);
            mProfileImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_tweet_user_profile_image);
            mTweetTextView = (TextView) itemView
                    .findViewById(R.id.list_item_tweet_tweet_text_view);
            mScreenNameTextView = (TextView) itemView
                    .findViewById(R.id.list_item_tweet_user_screen_name_text_view);
        }

        public void bindTweet(Tweet tweet) {
            mTweetTextView.setText(tweet.getText());
            if (tweet.getUser() != null) {
                mScreenNameTextView.setText(tweet.getUser().getScreenName());
                Glide.with(getActivity())
                        .load(tweet.getUser().getPhotoUrl()).into(mProfileImageView);
            }
        }
    }
}
