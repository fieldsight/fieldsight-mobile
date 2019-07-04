package org.bcss.collect.naxa.v3.network;

import android.arch.persistence.room.ColumnInfo;

public class ProjectNameTuple {
    @ColumnInfo(name = "project_id")
    public String projectId;

    @ColumnInfo(name = "created_date")
    public long created_date;

    @ColumnInfo(name = "status")
    public int status;


}
