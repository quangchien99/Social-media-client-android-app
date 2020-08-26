package com.qcp.facebookapp.interfaces;

import com.qcp.facebookapp.model.FriendList;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.model.Status;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @POST("profiles/")
    Call<Profile> createUser(@Body Profile profile);

    @DELETE("friendlist/{username}/{friendUsername}")
    Call<ResponseBody> deleteFriend(@Path("username") String username, @Path("friendUsername") String friendUsername);

    @POST("friendlist/{username}")
    Call<ResponseBody> addFriend(@Path("username") String username, @Body FriendList friendList);
}