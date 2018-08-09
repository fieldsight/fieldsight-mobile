package org.bcss.collect.naxa.generalforms.data;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormResponse implements Parcelable {


    @SerializedName(value = "submitted_by_username", alternate = {"submitted_by"})
    @Expose
    private String submittedByUsername;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("get_responces")
    @Expose
    private List<GetResponce> getResponces = null;
    @SerializedName("form_status")
    @Expose
    private Integer formStatus;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("instance")
    @Expose
    private Integer instance;
    @SerializedName("site")
    @Expose
    private Integer site;
    @SerializedName("project")
    @Expose
    private Integer project;
    @SerializedName("site_fxf")
    @Expose
    private Integer siteFxf;
    @SerializedName("project_fxf")
    @Expose
    private Integer projectFxf;

    @SerializedName("form_type")
    @Expose
    private FormType formType;

    public FormType getFormType() {
        return formType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<GetResponce> getGetResponces() {
        return getResponces;
    }

    public void setGetResponces(List<GetResponce> getResponces) {
        this.getResponces = getResponces;
    }

    public static String serialize(FormResponse form) {
        return new Gson().toJson(form);
    }

    public static String getSingleResponse(List<FormResponse> list, int index) {

        if (list == null || list.size() != 1) {
            return "";
        }


        return FormResponse.serialize(list.get(index));
    }

    public static FormResponse deserialize(String form) {
        return new Gson().fromJson(form, FormResponse.class);
    }


    public Integer getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(Integer formStatus) {
        this.formStatus = formStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getInstance() {
        return instance;
    }

    public void setInstance(Integer instance) {
        this.instance = instance;
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

    public Integer getSiteFxf() {
        return siteFxf;
    }

    public void setSiteFxf(Integer siteFxf) {
        this.siteFxf = siteFxf;
    }

    public Integer getProjectFxf() {
        return projectFxf;
    }

    public void setProjectFxf(Integer projectFxf) {
        this.projectFxf = projectFxf;
    }

    public String getSubmittedByUsername() {
        return submittedByUsername;
    }

    public void setSubmittedByUsername(String submittedByUsername) {
        this.submittedByUsername = submittedByUsername;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.submittedByUsername);
        dest.writeValue(this.id);
        dest.writeList(this.getResponces);
        dest.writeValue(this.formStatus);
        dest.writeString(this.date);
        dest.writeValue(this.instance);
        dest.writeValue(this.site);
        dest.writeValue(this.project);
        dest.writeValue(this.siteFxf);
        dest.writeValue(this.projectFxf);

    }

    public FormResponse() {
    }

    protected FormResponse(Parcel in) {
        this.submittedByUsername = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.getResponces = new ArrayList<GetResponce>();
        in.readList(this.getResponces, GetResponce.class.getClassLoader());
        this.formStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.date = in.readString();
        this.instance = (Integer) in.readValue(Integer.class.getClassLoader());
        this.site = (Integer) in.readValue(Integer.class.getClassLoader());
        this.project = (Integer) in.readValue(Integer.class.getClassLoader());
        this.siteFxf = (Integer) in.readValue(Integer.class.getClassLoader());
        this.projectFxf = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<FormResponse> CREATOR = new Parcelable.Creator<FormResponse>() {
        @Override
        public FormResponse createFromParcel(Parcel source) {
            return new FormResponse(source);
        }

        @Override
        public FormResponse[] newArray(int size) {
            return new FormResponse[size];
        }
    };
}
