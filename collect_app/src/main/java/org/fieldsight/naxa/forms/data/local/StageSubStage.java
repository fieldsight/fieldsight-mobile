
package org.fieldsight.naxa.forms.data.local;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StageSubStage {

    @SerializedName("stage_name")
    @Expose
    private String stageName;
    @SerializedName("stage_description")
    @Expose
    private String stageDescription;
    @SerializedName("stage_type")
    @Expose
    private List<Object> stageType ;
    @SerializedName("stage_order")
    @Expose
    private String stageOrder;
    @SerializedName("stage_weight")
    @Expose
    private String stageWeight;
    @SerializedName("substage_name")
    @Expose
    private String substageName;
    @SerializedName("substage_description")
    @Expose
    private String substageDescription;
    @SerializedName("substage_order")
    @Expose
    private String substageOrder;
    @SerializedName("substage_weight")
    @Expose
    private String substageWeight;
    @SerializedName("substage_tags")
    @Expose
    private List<Object> substageTags;
    @SerializedName("substage_regions")
    @Expose
    private List<Object> substageRegions;

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStageDescription() {
        return stageDescription;
    }

    public void setStageDescription(String stageDescription) {
        this.stageDescription = stageDescription;
    }

    public List<Object> getStageType() {
        return stageType;
    }

    public void setStageType(List<Object> stageType) {
        this.stageType = stageType;
    }

    public String getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(String stageOrder) {
        this.stageOrder = stageOrder;
    }

    public String getStageWeight() {
        return stageWeight;
    }

    public void setStageWeight(String stageWeight) {
        this.stageWeight = stageWeight;
    }

    public String getSubstageName() {
        return substageName;
    }

    public void setSubstageName(String substageName) {
        this.substageName = substageName;
    }

    public String getSubstageDescription() {
        return substageDescription;
    }

    public void setSubstageDescription(String substageDescription) {
        this.substageDescription = substageDescription;
    }

    public String getSubstageOrder() {
        return substageOrder;
    }

    public void setSubstageOrder(String substageOrder) {
        this.substageOrder = substageOrder;
    }

    public String getSubstageWeight() {
        return substageWeight;
    }

    public void setSubstageWeight(String substageWeight) {
        this.substageWeight = substageWeight;
    }

    public List<Object> getSubstageTags() {
        return substageTags;
    }

    public void setSubstageTags(List<Object> substageTags) {
        this.substageTags = substageTags;
    }

    public List<Object> getSubstageRegions() {
        return substageRegions;
    }

    public void setSubstageRegions(List<Object> substageRegions) {
        this.substageRegions = substageRegions;
    }

}
