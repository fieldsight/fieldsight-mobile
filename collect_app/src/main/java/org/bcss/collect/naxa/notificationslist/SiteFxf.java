
package org.bcss.collect.naxa.notificationslist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteFxf {

    @SerializedName("xf")
    @Expose
    private Xf xf;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Xf getXf() {
        return xf;
    }

    public void setXf(Xf xf) {
        this.xf = xf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
