package com.snapable.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public abstract class BaseObject implements Serializable {

    public String resourceUri;

    /**
     * The primary key of the object based on the resource uri.
     *
     * @return the resource primary key
     */
    @JsonIgnore
    public Long getPk() {
        if (resourceUri != null) {
            return getPkFromResourceUri(resourceUri);
        } else {
            return (long) 0;
        }
    }

    /**
     * Set the primary key of the object.
     *
     * @param pk the primary key to set
     */
    public void setPk(Long pk) {
        resourceUri = getResourceUriFromLong(pk);
    }

    /**
     * Get the primary key of the object based on the resource uri string.
     *
     * @param resource_uri the resource uri string to parse
     * @return the pk of the resource uri
     */
    @JsonIgnore
    public static Long getPkFromResourceUri(String resource_uri) {
        String[] resourceParts = resource_uri.split("/");
        return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

    @JsonIgnore
    public abstract String getResourceUriFromLong(Long pk);

}
