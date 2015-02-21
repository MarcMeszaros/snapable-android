package com.snapable.api.private_v1.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

abstract class BaseObject implements Serializable {

    public String resourceUri;

    /**
     * The primary key of the object based on the resource uri.
     *
     * @return the resource primary key
     */
    @JsonIgnore
    public long getPk() {
        if (resourceUri != null) {
            return getPkFromResourceUri(resourceUri);
        } else {
            return 0;
        }
    }

    /**
     * Set the primary key of the object.
     *
     * @param pk the primary key to set
     */
    public void setPk(long pk) {
        resourceUri = getResourceUriFromPk(pk);
    }

    /**
     * Get the primary key of the object based on the resource uri string.
     *
     * @param resource_uri the resource uri string to parse
     * @return the pk of the resource uri
     */
    @JsonIgnore
    public static long getPkFromResourceUri(String resource_uri) {
        String[] resourceParts = resource_uri.split("/");
        return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

    @JsonIgnore
    public String getResourceUriFromPk(long pk) {
        return getResourceUriFromPk(this.getClass(), pk);
    }

    @JsonIgnore
    public static String getResourceUriFromPk(Class<? extends BaseObject> clazz, long pk) {
        return String.format("/private_v1/%s/%d/", clazz.getSimpleName().toLowerCase(), pk);
    }

}
