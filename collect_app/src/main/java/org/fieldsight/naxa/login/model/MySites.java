package org.fieldsight.naxa.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MySites {


    @SerializedName("PROJECT")
    @Expose(serialize = false,deserialize = false)
    private Project project;
    @SerializedName("site")
    @Expose
    private Site site;

    /**
     *
     * @return
     *     The PROJECT
     */
    public Project getProject() {
        return project;
    }

    /**
     *
     * @param project
     *     The PROJECT
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
