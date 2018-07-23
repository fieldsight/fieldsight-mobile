
package org.odk.collect.naxa.stages.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Xf {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("id_string")
    @Expose
    private String jrFormId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }


}
