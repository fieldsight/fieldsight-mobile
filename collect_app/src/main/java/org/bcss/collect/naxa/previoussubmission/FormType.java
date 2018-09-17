
package org.bcss.collect.naxa.previoussubmission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormType {

    @SerializedName("is_staged")
    @Expose
    private Boolean isStaged;
    @SerializedName("is_survey")
    @Expose
    private Boolean isSurvey;
    @SerializedName("is_scheduled")
    @Expose
    private Boolean isScheduled;

    public Boolean getIsStaged() {
        return isStaged;
    }

    public void setIsStaged(Boolean isStaged) {
        this.isStaged = isStaged;
    }

    public Boolean getIsSurvey() {
        return isSurvey;
    }

    public void setIsSurvey(Boolean isSurvey) {
        this.isSurvey = isSurvey;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

}
