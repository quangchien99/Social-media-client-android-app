package com.qcp.facebookapp.model;

public class Link {
    private String uri;
    private String rel;

    public Link(String uri, String rel) {
        super();
        this.uri = uri;
        this.rel = rel;
    }

    public Link() {
        super();
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the rel
     */
    public String getRel() {
        return rel;
    }

    /**
     * @param rel the rel to set
     */
    public void setRel(String rel) {
        this.rel = rel;
    }
}

