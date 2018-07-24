
package org.odk.collect.naxa.stages.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.odk.collect.naxa.generalforms.data.Em;
import org.odk.collect.naxa.generalforms.data.FormResponse;

import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;


@Entity(tableName = "substage",
        foreignKeys = @ForeignKey(entity = Stage.class,
                parentColumns = "id",
                childColumns = "stageId",
                onDelete = CASCADE))

public class SubStage {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    private String stageId;

    @Ignore
    @SerializedName("stage_forms")
    @Expose
    private StageForms stageForms;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("order")
    @Expose
    private Integer order;

    @Ignore
    @SerializedName("tags")
    @Expose
    private List<String> tagIds;

    @SerializedName("responses_count")
    @Expose
    private String responseCount;

    @Ignore
    @SerializedName("em")
    @Expose
    private Em em;

    @SerializedName("project_stage_id")
    @Expose
    private Integer projectStageId;

    @Ignore
    @SerializedName("latest_submission")
    @Expose
    private List<FormResponse> latestSubmission = null;


    public SubStage() {

    }

    @Ignore
    public SubStage(Integer id, StageForms stageForms, String name, String description, Integer order, String responseCount, Em em, Integer projectStageId, List<FormResponse> latestSubmission) {
        this.id = id;
        this.stageForms = stageForms;
        this.name = name;
        this.description = description;
        this.order = order;
        this.responseCount = responseCount;
        this.em = em;
        this.projectStageId = projectStageId;
        this.latestSubmission = latestSubmission;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    public Integer getProjectStageId() {
        return projectStageId;
    }

    public void setProjectStageId(Integer projectStageId) {
        this.projectStageId = projectStageId;
    }

    public Em getEm() {
        return em;
    }

    public void setEm(Em em) {
        this.em = em;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StageForms getStageForms() {
        return stageForms;
    }

    public void setStageForms(StageForms stageForms) {
        this.stageForms = stageForms;
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

    public List<FormResponse> getLatestSubmission() {
        return latestSubmission;
    }

    public void setLatestSubmission(List<FormResponse> latestSubmission) {
        this.latestSubmission = latestSubmission;
    }

    public String getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(String responseCount) {
        this.responseCount = responseCount;
    }

}
