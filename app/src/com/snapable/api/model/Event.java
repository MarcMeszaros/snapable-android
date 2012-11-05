package com.snapable.api.model;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class Event {
    @JsonProperty("title")
    private String title;
    
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", this.title)
                .toString();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
