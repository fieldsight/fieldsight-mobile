package org.fieldsight.naxa.v3.network;

import androidx.room.ColumnInfo;

public class ProjectNameTuple {
    @ColumnInfo(name = "project_id")
    public String projectId;

    @ColumnInfo(name = "createdDate")
    public long createdDate;

    @ColumnInfo(name = "status")
    public int status;


}
