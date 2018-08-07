package org.bcss.collect.naxa.site;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 8/1/17
 * by nishon.tan@gmail.com
 */
@Entity(tableName = "site_types")
public class SiteType {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("identifier")
    @Expose
    private String identifer;

    @Expose
    @SerializedName("project")
    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SiteType() {

    }

    public void setIdentifer(String identifer) {
        this.identifer = identifer;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Ignore
    public SiteType(String id, String identifer, String projectId, String name) {
        this.id = id;
        this.identifer = identifer;
        this.name = name;
        this.projectId = projectId;

    }


    public String getName() {
        return name;
    }

    public String getIdentifer() {
        return identifer;
    }
}
