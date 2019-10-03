package org.fieldsight.naxa.generalforms.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmImage implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("image")
    @Expose
    private String image;

    private String imageName;

    private String imageLocalPath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageLocalPath() {

        return imageLocalPath;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.image);
        dest.writeString(this.imageName);
        dest.writeString(this.imageLocalPath);
    }

    public EmImage() {
    }

    private EmImage(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.image = in.readString();
        this.imageName = in.readString();
        this.imageLocalPath = in.readString();
    }

    public static final Parcelable.Creator<EmImage> CREATOR = new Parcelable.Creator<EmImage>() {
        @Override
        public EmImage createFromParcel(Parcel source) {
            return new EmImage(source);
        }

        @Override
        public EmImage[] newArray(int size) {
            return new EmImage[size];
        }
    };
}
