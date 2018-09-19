package org.bcss.collect.naxa.previoussubmission.model;

import android.arch.persistence.room.Embedded;

import com.google.common.base.Objects;

import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;

public class ScheduledFormAndSubmission {

    @Embedded
    private ScheduleForm scheduleForm;

    @Embedded
    private SubmissionDetail submissionDetail;

    public ScheduleForm getScheduleForm() {
        return scheduleForm;
    }

    public void setScheduleForm(ScheduleForm scheduleForm) {
        this.scheduleForm = scheduleForm;
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
        ScheduledFormAndSubmission that = (ScheduledFormAndSubmission) o;
        return Objects.equal(scheduleForm, that.scheduleForm) &&
                Objects.equal(submissionDetail, that.submissionDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(scheduleForm, submissionDetail);
    }
}
