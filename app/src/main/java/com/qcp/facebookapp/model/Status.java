package com.qcp.facebookapp.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Status implements Serializable {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("profile")
    @Expose
    private Profile profile;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("link")
    @Expose
    private List<Link> links = new ArrayList<>();

    public Status(long id, String status, Profile profile) {
        super();
        this.id = id;
        this.status = status;
        this.profile = profile;
    }

    public Status() {
        super();
    }

    /**
     * @return the created
     */
    public String getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
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
     * @return the links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "Status [id=" + id + ", status=" + status + ", profile=" + profile + "]";
    }

    public void addLink(String uri, String rel) {
        Link link = new Link();
        link.setUri(uri);
        link.setRel(rel);
        links.add(link);
    }


}

