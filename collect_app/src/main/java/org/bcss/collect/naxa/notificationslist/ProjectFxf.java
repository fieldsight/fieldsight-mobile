
package org.bcss.collect.naxa.notificationslist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectFxf {

    @SerializedName("xf")
    @Expose
    private Xf_ xf;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Xf_ getXf() {
        return xf;
    }

    public void setXf(Xf_ xf) {
        this.xf = xf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
