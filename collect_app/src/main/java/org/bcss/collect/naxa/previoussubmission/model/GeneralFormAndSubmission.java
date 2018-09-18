package org.bcss.collect.naxa.previoussubmission.model;

import android.arch.persistence.room.Embedded;

import com.google.common.base.Objects;

import org.bcss.collect.naxa.generalforms.data.GeneralForm;

public class GeneralFormAndSubmission {

    @Embedded
    private GeneralForm generalForm;

    @Embedded
    private SubmissionDetail submissionDetail;

    public GeneralForm getGeneralForm() {
        return generalForm;
    }

    public void setGeneralForm(GeneralForm generalForm) {
        this.generalForm = generalForm;
    }

    public SubmissionDetail getSubmissionDetail() {
        return submissionDetail;
    }

    public void setSubmissionDetail(SubmissionDetail submissionDetail) {
        this.submissionDetail = submissionDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralFormAndSubmission that = (GeneralFormAndSubmission) o;
        return Objects.equal(generalForm, that.generalForm) &&
                Objects.equal(submissionDetail, that.submissionDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(generalForm, submissionDetail);
    }
}
