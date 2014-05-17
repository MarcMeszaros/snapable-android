package com.snapable.api;

public abstract class BaseObject {

    public String resource_uri;

    /**
     * The primary key of the object based on the resource uri.
     *
     * @return the resource primary key
     */
    public Long getPk() {
        if (this.resource_uri != null) {
            return getPkFromResourceUri(this.resource_uri);
        } else {
            return Long.valueOf(0);
        }
    }

    /**
     * Set the primary key of the object.
     *
     * @param pk the primary key to set
     */
    public void setPk(Long pk) {
        resource_uri = getResourceUriFromLong(pk);
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

    public abstract String getResourceUriFromLong(Long pk);

}
