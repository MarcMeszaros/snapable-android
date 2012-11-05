package com.snapable.api.model;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class Pager<T> {

    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("objects")
    private T objects;

    public Meta getMeta() {
    	return meta;
    }
    
    public void setMeta(Meta meta) {
    	this.meta = meta;
    }
    
    public T getObjects() {
        return objects;
    }

    public void setObjects(T objects){
    	this.objects = objects;
    }

    public String toString() {
        return new ToStringBuilder(this)
        		.append("meta", meta)
                .append("objects", objects)
                .toString();
    }
}
