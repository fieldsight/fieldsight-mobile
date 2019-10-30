package org.fieldsight.naxa.v3.network;

import android.os.Parcel;
import android.os.Parcelable;

public class ProjectTypes implements Parcelable {
    int id, project;
    String identifier, name;
    boolean deleted;

    public ProjectTypes(int id, int project, String identifier, String name, boolean deleted) {
        this.id = id;
        this.project = project;
        this.identifier = identifier;
        this.name = name;
        this.deleted = deleted;
    }

    protected ProjectTypes(Parcel in) {
        id = in.readInt();
        project = in.readInt();
        identifier = in.readString();
        name = in.readString();
        deleted = in.readByte() != 0;
    }

    public static final Creator<ProjectTypes> CREATOR = new Creator<ProjectTypes>() {
        @Override
        public ProjectTypes createFromParcel(Parcel in) {
            return new ProjectTypes(in);
        }

        @Override
        public ProjectTypes[] newArray(int size) {
            return new ProjectTypes[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(project);
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeByte((byte) (deleted ? 1 : 0));
    }
}
