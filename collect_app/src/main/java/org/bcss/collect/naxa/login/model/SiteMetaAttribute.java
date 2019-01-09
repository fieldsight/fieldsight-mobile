package org.bcss.collect.naxa.login.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.common.GSONInstance;

import java.util.List;

public class SiteMetaAttribute implements Parcelable {

    @SerializedName("question_name")
    @Expose
    private String questionName;
    @SerializedName("is_deleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("question_text")
    @Expose
    private String questionText;
    @SerializedName("question_type")
    @Expose
    private String questionType;

    @SerializedName("question_placeholder")
    @Expose
    private String questionPlaceholder;


    @SerializedName("question_help")
    @Expose
    private String questionHelp;
    @SerializedName("mcq_options")
    @Expose
    private List<McqOption> mcqOptions = null;

    public SiteMetaAttribute(String questionName, String questionText, String questionType) {
        this.questionName = questionName;
        this.questionText = questionText;
        this.questionType = questionType;
    }




    public SiteMetaAttribute toSiteMetaAttribute(String json) {
        return GSONInstance.getInstance().fromJson(json, SiteMetaAttribute.class);
    }

    public String toJson(SiteMetaAttribute siteMetaAttribute) {
        return GSONInstance.getInstance().toJson(siteMetaAttribute);
    }
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionPlaceholder() {
        return questionPlaceholder;
    }

    public void setQuestionPlaceholder(String questionPlaceholder) {
        this.questionPlaceholder = questionPlaceholder;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionHelp() {
        return questionHelp;
    }

    public void setQuestionHelp(String questionHelp) {
        this.questionHelp = questionHelp;
    }

    public List<McqOption> getMcqOptions() {
        return mcqOptions;
    }

    public void setMcqOptions(List<McqOption> mcqOptions) {
        this.mcqOptions = mcqOptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionName);
        dest.writeValue(this.isDeleted);
        dest.writeString(this.questionText);
        dest.writeString(this.questionType);
        dest.writeString(this.questionPlaceholder);
        dest.writeString(this.questionHelp);
        dest.writeTypedList(this.mcqOptions);
    }

    protected SiteMetaAttribute(Parcel in) {
        this.questionName = in.readString();
        this.isDeleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.questionText = in.readString();
        this.questionType = in.readString();
        this.questionPlaceholder = in.readString();
        this.questionHelp = in.readString();
        this.mcqOptions = in.createTypedArrayList(McqOption.CREATOR);
    }

    public static final Creator<SiteMetaAttribute> CREATOR = new Creator<SiteMetaAttribute>() {
        @Override
        public SiteMetaAttribute createFromParcel(Parcel source) {
            return new SiteMetaAttribute(source);
        }

        @Override
        public SiteMetaAttribute[] newArray(int size) {
            return new SiteMetaAttribute[size];
        }
    };
}
