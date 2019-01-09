
package org.bcss.collect.naxa.stages.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.generalforms.data.Em;
import org.bcss.collect.naxa.generalforms.data.FormResponse;
import org.bcss.collect.naxa.stages.StringListTypeConvertor;

import java.util.List;

//
//@Entity(tableName = "substage",
//        foreignKeys = @ForeignKey(entity = Stage.class,
//                parentColumns = "id",
//                childColumns = "stageId",
//                onDelete = CASCADE))

@Entity(tableName = "substage")
public class SubStage {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    private String stageId;

    @ColumnInfo(name = "deployed_from")
    private String subStageDeployedFrom;


    @SerializedName("form")
    @Expose
    private String fsFormId;

    private String jrFormId;

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

    @TypeConverters(StringListTypeConvertor.class)
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

    private String lastSubmissionBy;
    private String lastSubmissionDateTime;


    public String getSubStageDeployedFrom() {
        return subStageDeployedFrom;
    }

    public void setSubStageDeployedFrom(String subStageDeployedFrom) {
        this.subStageDeployedFrom = subStageDeployedFrom;
    }

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
        this.jrFormId = stageForms.getXf().getJrFormId();
        this.fsFormId = stageForms.getId();
    }

    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
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

    public String getLastSubmissionBy() {
        return lastSubmissionBy;
    }

    public void setLastSubmissionBy(String lastSubmissionBy) {
        this.lastSubmissionBy = lastSubmissionBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubStage subStage = (SubStage) o;
        return Objects.equal(id, subStage.id) &&
                Objects.equal(stageId, subStage.stageId) &&
                Objects.equal(fsFormId, subStage.fsFormId) &&
                Objects.equal(jrFormId, subStage.jrFormId) &&
                Objects.equal(stageForms, subStage.stageForms) &&
                Objects.equal(name, subStage.name) &&
                Objects.equal(description, subStage.description) &&
                Objects.equal(order, subStage.order) &&
                Objects.equal(tagIds, subStage.tagIds) &&
                Objects.equal(responseCount, subStage.responseCount) &&
                Objects.equal(em, subStage.em) &&
                Objects.equal(projectStageId, subStage.projectStageId) &&
                Objects.equal(latestSubmission, subStage.latestSubmission) &&
                Objects.equal(lastSubmissionBy, subStage.lastSubmissionBy) &&
                Objects.equal(lastSubmissionDateTime, subStage.lastSubmissionDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, stageId, fsFormId, jrFormId, stageForms, name, description, order, tagIds, responseCount, em, projectStageId, latestSubmission, lastSubmissionBy, lastSubmissionDateTime);
    }

    public String getLastSubmissionDateTime() {
        return lastSubmissionDateTime;
    }

    public void setLastSubmissionDateTime(String lastSubmissionDateTime) {
        this.lastSubmissionDateTime = lastSubmissionDateTime;
    }

}
