package org.odk.collect.naxa.scheduled.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.naxa.generalforms.data.Em;
import org.odk.collect.naxa.generalforms.data.FormResponse;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "scheduled_form",
        primaryKeys = {"scheduleId", "formDeployedFrom"})
public class ScheduleForm {


    @NonNull
    private String formDeployedFrom;

    @NonNull
    @SerializedName("id")
    private String scheduleId;

    @SerializedName("form")
    private String fsFormId;

    @Ignore
    private FormResponse formResponse;

    @Ignore
    @SerializedName("em")
    @Expose
    private Em em;

    @Ignore
    @SerializedName("latest_submission")
    @Expose
    private List<FormResponse> latestSubmission = null;

    @SerializedName("schedule_level")
    @Expose
    private String scheduleLevel;

    @SerializedName("date_range_start")
    private String startDate;

    @SerializedName("date_range_end")
    private String endDate;

    @Ignore
    @SerializedName("selected_days")
    private ArrayList<String> frequency;


    @SerializedName("name")
    private String scheduleName;

    @SerializedName("site")
    private String siteId;

    @SerializedName("id_string")
    private String idString;

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setScheduleLevel(String scheduleLevel) {
        this.scheduleLevel = scheduleLevel;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public void setFrequencyArrayInString(String frequencyArrayInString) {
        this.frequencyArrayInString = frequencyArrayInString;
    }

    public void setFormDescFromXML(String formDescFromXML) {
        this.formDescFromXML = formDescFromXML;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setLastFilledDateTime(String lastFilledDateTime) {
        this.lastFilledDateTime = lastFilledDateTime;
    }

    @SerializedName("project")
    @Expose
    private String project;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<FormResponse> getLatestSubmission() {
        return latestSubmission;
    }

    public Boolean getFormDeployed() {
        return isFormDeployed;
    }

    public Boolean isFormNotDeployed() {
        return !isFormDeployed;
    }

    public void setFormDeployed(Boolean formDeployed) {
        isFormDeployed = formDeployed;
    }

    @SerializedName("is_deployed")
    private Boolean isFormDeployed;

    public String getIdString() {
        return idString;
    }

    public Em getEm() {
        return em;
    }

    public void setEm(Em em) {
        this.em = em;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    //for getting the value

    public String getFrequencyArrayInString() {
        return frequencyArrayInString;
    }

    private String frequencyArrayInString;

    public String getScheduleName() {
        return scheduleName;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    @Ignore
    private FormDetails formDetails;

    private String jrFormId;
    private String siteName;

    @SerializedName("form_name")
    private String formName;
    private String formDescFromXML;

    public ScheduleForm() {
    }

    private String hash;

    public String getLastFilledDateTime() {
        return lastFilledDateTime;
    }

    private String lastFilledDateTime;

    public String getFormDescFromXML() {
        return formDescFromXML;
    }

    public String getHash() {
        return hash;
    }


    public String getFormDeployedFrom() {
        return formDeployedFrom;
    }

    public void setFormDeployedFrom(String formDeployedFrom) {
        this.formDeployedFrom = formDeployedFrom;
    }


    public FormResponse getFormResponse() {


        return formResponse;
    }

    public void setLatestSubmission(List<FormResponse> latestSubmission) {
        this.latestSubmission = latestSubmission;
    }


    public ScheduleForm(String startDate, String endDate, String frequencyArrayInString, String fsFormId, String scheduleName, String scheduleId, FormDetails formDetails, String siteId, String jrFormId, String siteName, String formName, String formDescFromXML, String hash
            , String lastFilledDateTime, String formDeployedFrom, FormResponse formResponse, String scheduleLevel) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequencyArrayInString = frequencyArrayInString;
        this.fsFormId = fsFormId;
        this.scheduleName = scheduleName;
        this.scheduleId = scheduleId;
        this.formDetails = formDetails;
        this.siteId = siteId;
        this.jrFormId = jrFormId;
        this.siteName = siteName;
        this.formName = formName;
        this.formDescFromXML = formDescFromXML;
        this.hash = hash;
        this.lastFilledDateTime = lastFilledDateTime;
        this.formDeployedFrom = formDeployedFrom;
        this.formResponse = formResponse;
        this.scheduleLevel = scheduleLevel;
    }

    public String getScheduleLevel() {
        return scheduleLevel;
    }


    public FormDetails getFormDetails() {
        return formDetails;
    }

    public void setFormDetails(FormDetails formDetails) {
        this.formDetails = formDetails;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getFrequency() {
        return frequency;
    }

    public void setFrequency(ArrayList<String> frequency) {
        this.frequency = frequency;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }

    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleForm that = (ScheduleForm) o;
        return Objects.equal(formResponse, that.formResponse) &&
                Objects.equal(em, that.em) &&
                Objects.equal(latestSubmission, that.latestSubmission) &&
                Objects.equal(scheduleLevel, that.scheduleLevel) &&
                Objects.equal(startDate, that.startDate) &&
                Objects.equal(endDate, that.endDate) &&
                Objects.equal(frequency, that.frequency) &&
                Objects.equal(fsFormId, that.fsFormId) &&
                Objects.equal(scheduleName, that.scheduleName) &&
                Objects.equal(scheduleId, that.scheduleId) &&
                Objects.equal(siteId, that.siteId) &&
                Objects.equal(idString, that.idString) &&
                Objects.equal(project, that.project) &&
                Objects.equal(isFormDeployed, that.isFormDeployed) &&
                Objects.equal(frequencyArrayInString, that.frequencyArrayInString) &&
                Objects.equal(formDetails, that.formDetails) &&
                Objects.equal(jrFormId, that.jrFormId) &&
                Objects.equal(siteName, that.siteName) &&
                Objects.equal(formName, that.formName) &&
                Objects.equal(formDescFromXML, that.formDescFromXML) &&
                Objects.equal(hash, that.hash) &&
                Objects.equal(lastFilledDateTime, that.lastFilledDateTime) &&
                Objects.equal(formDeployedFrom, that.formDeployedFrom);
    }


}
