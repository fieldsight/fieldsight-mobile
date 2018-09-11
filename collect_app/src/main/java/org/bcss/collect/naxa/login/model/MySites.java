package org.bcss.collect.naxa.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class MySites {

    @SerializedName("project")
    @Expose
    private Project project;
    @SerializedName("site")
    @Expose
    private Site site;

    /**
     *
     * @return
     *     The project
     */
    public Project getProject() {
        return project;
    }

    /**
     *
     * @param project
     *     The project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     *
     * @return
     *     The site
     */
    public Site getSite() {
        return site;
    }

    /**
     *
     * @param site
     *     The site
     */
    public void setSite(Site site) {
        this.site = site;
    }

}
