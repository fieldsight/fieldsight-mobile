package org.bcss.collect.naxa.stages.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.common.Constant;

import java.util.ArrayList;


@Entity(tableName = "stages",
        primaryKeys = {"id", "formDeployedFrom"})
public class Stage {

    @NonNull
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("project_stage_id")
    @Expose
    private Integer projectStageId;

    @Ignore
    @SerializedName("parent")
    @Expose
    private ArrayList<SubStage> subStage;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("order")
    @Expose
    private Integer order;

    @SerializedName("date_created")
    @Expose
    private String dateCreated;

    @SerializedName("date_modified")
    @Expose
    private String dateModified;

    @SerializedName("site")
    @Expose
    private Integer site;

    @SerializedName("project")
    @Expose
    private Integer project;

    @Ignore
    private String mockStages;

    @NonNull
    private String formDeployedFrom;

    public Stage() {

    }

    @Ignore
    public Stage(String id, Integer projectStageId, ArrayList<SubStage> subStage, String name, String description, Integer order, String dateCreated, String dateModified, Integer site, Integer project, String mockStages) {
        this.id = id;
        this.projectStageId = projectStageId;
        this.subStage = subStage;
        this.name = name;
        this.description = description;
        this.order = order;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.site = site;
        this.project = project;
        this.mockStages = mockStages;
        this.formDeployedFrom = project != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;

    }

    public String getFormDeployedFrom() {
        return project != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
    }

    public void setFormDeployedFrom(String formDeployedFrom) {
        this.formDeployedFrom = formDeployedFrom;
    }

    public String getMockStages() {
        return mockStages;
    }

    public void setMockStages(String mockStages) {
        this.mockStages = mockStages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<SubStage> getSubStage() {
        return subStage;
    }

    public Integer getProjectStageId() {
        return projectStageId;
    }

    public void setProjectStageId(Integer projectStageId) {
        this.projectStageId = projectStageId;
    }

    public void setSubStage(ArrayList<SubStage> subStage) {
        this.subStage = subStage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    public boolean hasAllSubStageComplete() {
        return true;
    }
}
