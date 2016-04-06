package com.bignerdranch.android.initialtwittersyncadapter.web;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by SamMyxer on 4/6/16.
 *
 * Post the data and use Response object so we can get the body data. Body data is returned
 * as a string instead of json
 */
public interface AuthenticationInterface {

    @POST("/oauth/request_token")
    void fetchRequestToken(@Body String body, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/oauth/access_token")
    void fetchAccessToken(@Field("oauth_verifier") String verifier,
                          Callback<Response> callback);
}
