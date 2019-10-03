
package org.fieldsight.naxa.stages.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Xf implements Parcelable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("id_string")
    @Expose
    private String jrFormId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeValue(this.id);
        dest.writeString(this.jrFormId);
    }

    public Xf() {
    }

    protected Xf(Parcel in) {
        this.title = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.jrFormId = in.readString();
    }

    public static final Parcelable.Creator<Xf> CREATOR = new Parcelable.Creator<Xf>() {
        @Override
        public Xf createFromParcel(Parcel source) {
            return new Xf(source);
        }

        @Override
        public Xf[] newArray(int size) {
            return new Xf[size];
        }
    };
}
