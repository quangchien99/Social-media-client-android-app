package com.qcp.facebookapp.interfaces;

import com.qcp.facebookapp.model.Comment;
import com.qcp.facebookapp.model.FriendList;
import com.qcp.facebookapp.model.Like;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.model.Status;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RequestAPI {
    @GET("profiles/{username}")
    Call<Profile> getProfile(@Path("username") String username);

    @GET("profiles")
    Call<List<Profile>> getAllProfiles();

    @GET("statuses")
    Call<List<Status>> getAllStatuses();

    @GET("friendlist/{username}")
    Call<FriendList> getFriendList(@Path("username") String username);


    @PUT("profiles/{username}")
    Call<ResponseBody> updateProfile(@Path("username") String username, @Body Profile profile);

    @POST("profiles/")
    Call<Profile> createUser(@Body Profile profile);

    @DELETE("friendlist/{username}/{friendUsername}")
    Call<ResponseBody> deleteFriend(@Path("username") String username, @Path("friendUsername") String friendUsername);

    @POST("friendlist/")
    Call<ResponseBody> addFriend(@Body FriendList friendList);

    @POST("statuses/")
    Call<ResponseBody> postStatus(@Body Status status);

    @GET("comments")
    Call<List<Comment>> getAllComments();

    @POST("comments/")
    Call<ResponseBody> addComment(@Body Comment comment);

    @GET("likes")
    Call<List<Like>> getAllLikes();

    @POST("likes/")
    Call<ResponseBody> addLike(@Body Like like);

    @DELETE("likes/{id}")
    Call<ResponseBody> deleteLike(@Path("id") long id);


}
