package com.example.redditapp;

import com.example.redditapp.Account.CheckLogin;
import com.example.redditapp.comments.CheckComment;
import com.example.redditapp.model.Feed;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Get RSS feed from Reddit
 */
public interface FeedAPI {

    //Static feed name
//    @GET("earthporn/.rss")
//    Call<Feed> getFeed();

    //Non-static feed name
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

    //this one works
    //login and get session params
    @POST("{user}")
    Call<CheckLogin> signIn(
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );

    //post comment
    @POST("{comment}")
    Call<CheckComment> submitComment(
            @HeaderMap Map<String, String> headers,
            @Path("comment") String username,
            @Query("parent") String parent,
            @Query("text") String text
    );

    //for some reason didn't work with header
    @POST("{user}")
    Call<CheckLogin> signIn(
            @HeaderMap Map<String, String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );

    //to get raw json response
    @POST("{user}")
    Call<ResponseBody> getBody(
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );

    //to get raw json response
    @POST("{user}")
    Call<ResponseBody> getBody(
            @HeaderMap Map<String, String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );
}
