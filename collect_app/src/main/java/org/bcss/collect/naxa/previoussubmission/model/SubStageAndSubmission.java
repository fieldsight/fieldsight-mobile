package org.bcss.collect.naxa.previoussubmission.model;

import android.arch.persistence.room.Embedded;

import com.google.common.base.Objects;

import org.bcss.collect.naxa.stages.data.SubStage;

public class SubStageAndSubmission {
    @Embedded
    private SubStage subStage;

    @Embedded
    private SubmissionDetail submissionDetail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubStageAndSubmission that = (SubStageAndSubmission) o;
        return Objects.equal(subStage, that.subStage) &&
                Objects.equal(submissionDetail, that.submissionDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(subStage, submissionDetail);
    }

    public SubStage getSubStage() {
        return subStage;

    }

    public void setSubStage(SubStage subStage) {
        this.subStage = subStage;
    }

    public SubmissionDetail getSubmissionDetail() {
        return submissionDetail;
    }

    public void setSubmissionDetail(SubmissionDetail submissionDetail) {
        this.submissionDetail = submissionDetail;
    }


}
