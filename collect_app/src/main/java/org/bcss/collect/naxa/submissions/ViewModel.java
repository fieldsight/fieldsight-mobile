package org.bcss.collect.naxa.submissions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

import java.util.ArrayList;

/**
 * Created on 11/29/17
 * by nishon.tan@gmail.com
 */

public class ViewModel implements Parcelable {

    private String name;
    private String desc;
    private String id;
    private String secondaryId;
    private String pictureUrl;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public ViewModel(String name, String desc, String id, String secondaryId) {
        this.name = name;
        this.desc = desc;
        this.id = id;
        this.secondaryId = secondaryId;
    }

    public String getSecondaryId() {
        return secondaryId;
    }


    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public static ArrayList<ViewModel> getDummyList(int totalItems) {
        ArrayList<ViewModel> viewModels = new ArrayList<>();

        for (int i = 0; i <= totalItems; i++) {
            viewModels.add(new ViewModel("name " + i, "Description " + i, String.valueOf(i), String.valueOf(i)));
        }

        return viewModels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewModel viewModel = (ViewModel) o;
        return Objects.equal(name, viewModel.name) &&
                Objects.equal(desc, viewModel.desc) &&
                Objects.equal(id, viewModel.id) &&
                Objects.equal(secondaryId, viewModel.secondaryId) &&
                Objects.equal(pictureUrl, viewModel.pictureUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, desc, id, secondaryId, pictureUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeString(this.id);
        dest.writeString(this.secondaryId);
        dest.writeString(this.pictureUrl);
    }

    protected ViewModel(Parcel in) {
        this.name = in.readString();
        this.desc = in.readString();
        this.id = in.readString();
        this.secondaryId = in.readString();
        this.pictureUrl = in.readString();
    }

    public static final Parcelable.Creator<ViewModel> CREATOR = new Parcelable.Creator<ViewModel>() {
        @Override
        public ViewModel createFromParcel(Parcel source) {
            return new ViewModel(source);
        }

        @Override
        public ViewModel[] newArray(int size) {
            return new ViewModel[size];
        }
    };
}
