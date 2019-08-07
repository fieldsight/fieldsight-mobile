package org.bcss.collect.naxa.generalforms.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.common.GSONInstance;

public class GetResponce implements Parcelable {

    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("question")
    @Expose
    private Object question;
    @SerializedName("type")
    @Expose
    private String type;

    public String getAnswer() {
        return answer;
    }

    public Object getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean isKeyValuePair = question.toString().startsWith("{") && question.toString().endsWith("}") && question.toString().contains("=") && question.toString().contains(",");

        dest.writeString(this.answer);
        if (isKeyValuePair) {
            dest.writeString(GSONInstance.getInstance().toJson(this.question));
        } else {
            dest.writeString(this.question.toString());
        }
        dest.writeString(this.type);
    }

    public GetResponce() {
    }

    protected GetResponce(Parcel in) {
        this.answer = in.readString();
        this.question = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<GetResponce> CREATOR = new Parcelable.Creator<GetResponce>() {
        @Override
        public GetResponce createFromParcel(Parcel source) {
            return new GetResponce(source);
        }

        @Override
        public GetResponce[] newArray(int size) {
            return new GetResponce[size];
        }
    };
}
