package com.bignerdranch.android.initialtwittersyncadapter.controller;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bignerdranch.android.initialtwittersyncadapter.account.Authenticator;
import com.bignerdranch.android.initialtwittersyncadapter.web.AuthenticationInterface;
import com.bignerdranch.android.initialtwittersyncadapter.web.AuthorizationInterceptor;
import com.bignerdranch.android.initialtwittersyncadapter.web.TwitterOauthHelper;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Get here from TweetListFragment checking to see if we have AccessToken in onStart();
 * - If we don't then we launch AuthenticationActivity
 *
 *
 * 1. Make call to get the request token
 * 2. onSuccess --> Open the twitter auth w/ web view
 * 3. User logs in and if callback url is BNR.com then make access token call
 * 4. On Success --> Setup account w/ system and finish this activity
 */
public class AuthenticationActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "AuthenticationActivity";
    private static final String EXTRA_ACCOUNT_TYPE =
            "com.bignerdranch.android.twittersyncadapter.ACCOUNT_TYPE";
    private static final String EXTRA_AUTH_TYPE =
            "com.bignerdranch.android.twittersyncadapter.AUTH_TYPE";
    private static final String TWITTER_ENDPOINT = "https://api.twitter.com";
    private static final String TWITTER_OAUTH_ENDPOINT =
            "https://api.twitter.com/oauth/authorize";
    private static  final String CALLBACK_URL = "http://www.bignerdranch.com";
    public static final String OAUTH_TOKEN_SECRET_KEY =
            "com.bignerdranch.android.twitersyncadapter.OAUTH_TOKEN_SECRET";

    private WebView webView;
    private RestAdapter restAdapter;
    private TwitterOauthHelper twitterOauthHelper;
    private AuthenticationInterface authenticationInterface;

    public static Intent newIntent(
            Context context, String accountType, String authTokenType) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.putExtra(EXTRA_ACCOUNT_TYPE, accountType);
        intent.putExtra(EXTRA_AUTH_TYPE, authTokenType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);
        webView.setWebViewClient(webViewClient);

        twitterOauthHelper = TwitterOauthHelper.get();
        twitterOauthHelper.resetOauthToken();

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new AuthorizationInterceptor());

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(TWITTER_ENDPOINT)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        authenticationInterface = restAdapter.create(AuthenticationInterface.class);
        authenticationInterface.fetchRequestToken("", new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Uri uri = getResponseUri(response);
                String oauthToken = uri.getQueryParameter("oauth_token");
                Uri twitterOAuthUri = Uri.parse(TWITTER_OAUTH_ENDPOINT).buildUpon()
                        .appendQueryParameter("oauth_token", oauthToken)
                        .build();

                webView.loadUrl(twitterOAuthUri.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Failed to fetch request token", error);
            }
        });
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            if(!url.contains(CALLBACK_URL)) {
                return true;
            }

            Uri callbackUri = Uri.parse(url);
            String oauthToken = callbackUri.getQueryParameter("oauth_token");
            String oauthVerifier = callbackUri.getQueryParameter("oauth_verifier");

            twitterOauthHelper.setOauthToken(oauthToken, null);

            authenticationInterface.fetchAccessToken(
                    oauthVerifier, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            Uri uri = getResponseUri(response);
                            String oauthToken = uri.getQueryParameter("oauth_token");
                            String oauthTokenSecret =
                                    uri.getQueryParameter("oauth_token_secret");
                            twitterOauthHelper.setOauthToken(oauthToken, oauthTokenSecret);

                            setupAccount(oauthToken, oauthTokenSecret);

                            final Intent intent = createAccountManagerIntent(oauthToken);

                            setAccountAuthenticatorResult(intent.getExtras());
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    }
            );
            return true;
        }
    };

    private Intent createAccountManagerIntent(String oauthToken) {
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Authenticator.ACCOUNT_NAME);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE,
                getIntent().getStringExtra(EXTRA_ACCOUNT_TYPE));
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, oauthToken);
        return intent;
    }

    private void setupAccount(String oauthToken, String oauthTokenSecret) {
        String accountType = getIntent().getStringExtra(EXTRA_ACCOUNT_TYPE);
        final Account account = new Account(Authenticator.ACCOUNT_NAME, accountType);
        String authTokenType = getIntent().getStringExtra(EXTRA_AUTH_TYPE);

        AccountManager accountManager =
                AccountManager.get(AuthenticationActivity.this);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, authTokenType, oauthToken);
        accountManager.setUserData(account, OAUTH_TOKEN_SECRET_KEY, oauthTokenSecret);
    }

    private Uri getResponseUri(Response response) {
        String responseBody =
                new String (((TypedByteArray) response.getBody()).getBytes());
        String parseUrl = "http://localhost?" + responseBody;
        return Uri.parse(parseUrl);
    }
}
