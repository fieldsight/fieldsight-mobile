package org.fieldsight.naxa.generalforms.data;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.fieldsight.naxa.generalforms.EmImageTypeConverter;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "educational_materials")
public class Em implements Parcelable {


    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("em_images")
    @Expose
    @TypeConverters(EmImageTypeConverter.class)
    private List<EmImage> emImages = null;

    @SerializedName("is_pdf")
    @Expose
    @Ignore
    private Boolean isPdf;

    @SerializedName("pdf")
    @Expose
    private String pdf;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("text")
    @Expose
    private String text;

    @PrimaryKey
    @NonNull
    @SerializedName("fsxf")
    private String fsFormId;

    @NonNull
    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(@NonNull String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public void setPdf(Boolean pdf) {
        isPdf = pdf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<EmImage> getEmImages() {
        return emImages;
    }

    public void setEmImages(List<EmImage> emImages) {
        this.emImages = emImages;
    }


    public void setIsPdf(Boolean isPdf) {
        this.isPdf = isPdf;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeList(this.emImages);
        dest.writeValue(this.isPdf);
        dest.writeString(this.pdf);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.fsFormId);
    }

    public Em() {
    }

    protected Em(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.emImages = new ArrayList<EmImage>();
        in.readList(this.emImages, EmImage.class.getClassLoader());
        this.isPdf = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.pdf = in.readString();
        this.title = in.readString();
        this.text = in.readString();
        this.fsFormId = in.readString();
    }

    public static final Parcelable.Creator<Em> CREATOR = new Parcelable.Creator<Em>() {
        @Override
        public Em createFromParcel(Parcel source) {
            return new Em(source);
        }

        @Override
        public Em[] newArray(int size) {
            return new Em[size];
        }
    };
}
