package org.bcss.collect.naxa.submissions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.generalforms.data.FormResponse;

import java.util.List;

public class FormHistoryResponse {

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("next")
    @Expose
    private String next;

    @SerializedName("previous")
    @Expose

    private Object previous;
    @SerializedName("results")
    @Expose
    private List<FormResponse> results = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    public List<FormResponse> getResults() {
        return results;
    }

    public void setResults(List<FormResponse> results) {
        this.results = results;
    }

}
