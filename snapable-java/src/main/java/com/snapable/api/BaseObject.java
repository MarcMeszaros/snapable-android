package com.snapable.api;

public class BaseObject {

    public String resource_uri;

    /**
     * The primary key of the object based on the resource uri.
     *
     * @return the resource primary key
     */
    public Long getPk() {
        return getPkFromResourceUri(this.resource_uri);
    }

    /**
     * Get the primary key of the object based on the resource uri string.
     *
     * @param resource_uri the resource uri string to parse
     * @return the pk of the resource uri
     */
    public static Long getPkFromResourceUri(String resource_uri) {
        String[] resourceParts = resource_uri.split("/");
        return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

}
