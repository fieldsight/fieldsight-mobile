package org.odk.collect.naxa.generalforms;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.common.base.Objects;

import org.odk.collect.android.logic.FormDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * General fomrs forms
 */

public class GeneralForm implements Parcelable {


    private String siteId;
    private String jrFormId;
    private String fsFormId;
    private String siteName;
    private String formName;
    private String formDescFromXML;
    private String hash;
    private FormDetails formDetails;
    private String error;
    private String status;
    private String lastFilledDateTime;
    private String projectId;
    private FormResponse formResponse;
    private String responseCount;

    public String getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(String responseCount) {
        this.responseCount = responseCount;
    }

    public FormResponse getFormResponse() {
        return formResponse;
    }

    public void setFormResponse(FormResponse formResponse) {
        this.formResponse = formResponse;
    }

    public String getFormDeployedFrom() {
        return formDeployedFrom;
    }

    public void setFormDeployedFrom(String formDeployedFrom) {
        this.formDeployedFrom = formDeployedFrom;
    }

    private String formDeployedFrom;

    public String getLastFilledDateTime() {
        return lastFilledDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFormDescFromXML(String formDescFromXML) {
        this.formDescFromXML = formDescFromXML;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public FormDetails getFormDetails() {
        return formDetails;
    }

    public void setFormDetails(FormDetails formDetails) {
        this.formDetails = formDetails;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFormDescFromXML() {
        return formDescFromXML;
    }

    public String getHash() {
        return hash;
    }


    public GeneralForm(String siteId, String jrFormId, String fsFormId, String siteName, String formName, String formDescFromXML, String hash, FormDetails formDetails, String status, String lastFilledDateTime, String projectId, String formDeployedFrom, FormResponse formResponse, String responseCount) {
        this.siteId = siteId;
        this.jrFormId = jrFormId;
        this.fsFormId = fsFormId;
        this.siteName = siteName;
        this.formName = formName;
        this.formDescFromXML = formDescFromXML;
        this.hash = hash;
        this.formDetails = formDetails;
        this.status = status;
        this.lastFilledDateTime = lastFilledDateTime;
        this.projectId = projectId;
        this.formDeployedFrom = formDeployedFrom;
        this.formResponse = formResponse;
        this.responseCount = responseCount;
    }

    public String getProjectId() {
        return projectId;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.siteId);
        dest.writeString(this.jrFormId);
        dest.writeString(this.fsFormId);
        dest.writeString(this.siteName);
        dest.writeString(this.formName);
        dest.writeString(this.formDescFromXML);
        dest.writeString(this.hash);
        dest.writeSerializable(this.formDetails);
        dest.writeString(this.error);
        dest.writeString(this.status);
        dest.writeString(this.lastFilledDateTime);
        dest.writeString(this.projectId);
        dest.writeString(this.formDeployedFrom);
    }

    protected GeneralForm(Parcel in) {
        this.siteId = in.readString();
        this.jrFormId = in.readString();
        this.fsFormId = in.readString();
        this.siteName = in.readString();
        this.formName = in.readString();
        this.formDescFromXML = in.readString();
        this.hash = in.readString();
        this.formDetails = (FormDetails) in.readSerializable();
        this.error = in.readString();
        this.status = in.readString();
        this.lastFilledDateTime = in.readString();
        this.projectId = in.readString();
        this.formDeployedFrom = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralForm that = (GeneralForm) o;
        return Objects.equal(siteId, that.siteId) &&
                Objects.equal(jrFormId, that.jrFormId) &&
                Objects.equal(fsFormId, that.fsFormId) &&
                Objects.equal(siteName, that.siteName) &&
                Objects.equal(formName, that.formName) &&
                Objects.equal(formDescFromXML, that.formDescFromXML) &&
                Objects.equal(hash, that.hash) &&
                Objects.equal(formDetails, that.formDetails) &&
                Objects.equal(error, that.error) &&
                Objects.equal(status, that.status) &&
                Objects.equal(lastFilledDateTime, that.lastFilledDateTime) &&
                Objects.equal(projectId, that.projectId) &&
                Objects.equal(formResponse, that.formResponse) &&
                Objects.equal(responseCount, that.responseCount) &&
                Objects.equal(formDeployedFrom, that.formDeployedFrom);
    }


    public static ArrayList<GeneralForm> getGeneralFormSortedByName(ArrayList<GeneralForm> employeeList) {

        Collections.sort(employeeList, new Comparator<GeneralForm>() {
            @Override
            public int compare(GeneralForm a1, GeneralForm a2) {
                return a1.getFormName().compareTo(a2.getFormName());
            }
        });

        return employeeList;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(siteId, jrFormId, fsFormId, siteName, formName, formDescFromXML, hash, formDetails, error, status, lastFilledDateTime, projectId, formResponse, responseCount, formDeployedFrom);
    }

    public static final Parcelable.Creator<GeneralForm> CREATOR = new Parcelable.Creator<GeneralForm>() {
        @Override
        public GeneralForm createFromParcel(Parcel source) {
            return new GeneralForm(source);
        }

        @Override
        public GeneralForm[] newArray(int size) {
            return new GeneralForm[size];
        }
    };
}
