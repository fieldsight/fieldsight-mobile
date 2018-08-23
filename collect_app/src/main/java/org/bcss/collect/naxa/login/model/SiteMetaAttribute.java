package org.bcss.collect.naxa.login.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.common.GSONInstance;

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


    public SiteMetaAttribute(String questionName, String questionText, String questionType) {
        this.questionName = questionName;
        this.questionText = questionText;
        this.questionType = questionType;
    }

    protected SiteMetaAttribute(Parcel in) {
        questionName = in.readString();
        byte tmpIsDeleted = in.readByte();
        isDeleted = tmpIsDeleted == 0 ? null : tmpIsDeleted == 1;
        questionText = in.readString();
        questionType = in.readString();
    }


    public SiteMetaAttribute toSiteMetaAttribute(String json) {
        return GSONInstance.getInstance().fromJson(json, SiteMetaAttribute.class);
    }

    public String toJson(SiteMetaAttribute siteMetaAttribute) {
        return GSONInstance.getInstance().toJson(siteMetaAttribute);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionName);
        dest.writeByte((byte) (isDeleted == null ? 0 : isDeleted ? 1 : 2));
        dest.writeString(questionText);
        dest.writeString(questionType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SiteMetaAttribute> CREATOR = new Creator<SiteMetaAttribute>() {
        @Override
        public SiteMetaAttribute createFromParcel(Parcel in) {
            return new SiteMetaAttribute(in);
        }

        @Override
        public SiteMetaAttribute[] newArray(int size) {
            return new SiteMetaAttribute[size];
        }
    };

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

}
