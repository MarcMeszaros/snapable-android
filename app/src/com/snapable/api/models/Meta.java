package com.snapable.api.models;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class Meta {
	@JsonProperty("limit")
    private long limit;
	@JsonProperty("next")
    private String next;
	@JsonProperty("offset")
    private long offset;
	@JsonProperty("previous")
    private String previous;
	@JsonProperty("total_count")
    private long totalCount;
    
    public String toString() {
        return new ToStringBuilder(this)
                .append("limit", this.limit)
                .append("next", this.next)
                .append("offset", this.offset)
                .append("previous", this.previous)
                .append("totalCount", this.totalCount)
                .toString();
    }

    public long getLimit() {
        return this.limit;
    }

    public void setTitle(long limit) {
        this.limit = limit;
    }
    
    public String getNext() {
        return this.next;
    }

    public void setNext(String next) {
        this.next = next;
    }
    
    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
    
    public String getPrevious() {
        return this.previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
    
    public long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
