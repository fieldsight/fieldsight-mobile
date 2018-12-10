package org.bcss.collect.naxa.login.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class McqOption implements Parcelable {

    @SerializedName("option_text")
    @Expose
    private String optionText;

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.optionText);
    }

    public McqOption() {
    }

    protected McqOption(Parcel in) {
        this.optionText = in.readString();
    }

    public static final Parcelable.Creator<McqOption> CREATOR = new Parcelable.Creator<McqOption>() {
        @Override
        public McqOption createFromParcel(Parcel source) {
            return new McqOption(source);
        }

        @Override
        public McqOption[] newArray(int size) {
            return new McqOption[size];
        }
    };
}