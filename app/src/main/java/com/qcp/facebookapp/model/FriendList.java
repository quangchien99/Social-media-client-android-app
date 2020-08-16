package com.qcp.facebookapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendList {
    @SerializedName("profile")
    @Expose
    private Profile profile;
    @SerializedName("friend")
    @Expose
    private List<Profile> friend;

    public FriendList(Profile profile, List<Profile> friend) {
        super();
        this.profile = profile;
        this.friend = friend;
    }

    /**
     * @return the profile
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * @return the friend
     */
    public List<Profile> getFriend() {
        return friend;
    }

    /**
     * @param friend the friend to set
     */
    public void setFriend(List<Profile> friend) {
        this.friend = friend;
    }

    public FriendList() {
        super();
    }

    @Override
    public String toString() {
        return "FriendList [profile=" + profile + ", friend=" + friend + "]";
    }
}
