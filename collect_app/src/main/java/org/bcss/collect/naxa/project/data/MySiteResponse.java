package org.bcss.collect.naxa.project.data;

import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.login.model.MySites;

import java.util.List;

public class MySiteResponse {
    @SerializedName("count")
    private String count;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private List<MySites> result;


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<MySites> getResult() {
        return result;
    }

    public void setResult(List<MySites> result) {
        this.result = result;
    }
}
