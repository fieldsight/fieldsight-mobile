package org.bcss.collect.naxa.common.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Entity(tableName = "site_overide_ids")
public class SiteOveride {
    @PrimaryKey
    @NonNull
    private String projectId;
    private String generalFormIds;
    private String scheduleFormIds;
    private String stagedFormIds;

    @Ignore
    private List<String> generalformIdList;

    @Ignore
    private List<String> scheduleFormIdList;

    @Ignore
    private List<String> stagedformIdList;


    public SiteOveride() {

    }

    @Ignore
    public SiteOveride(@NonNull String projectId, String generalFormIds, String scheduleFormIds, String stagedFormIds) {
        this.projectId = projectId;
        this.generalFormIds = generalFormIds;
        this.scheduleFormIds = scheduleFormIds;
        this.stagedFormIds = stagedFormIds;
    }

    @NonNull
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(@NonNull String projectId) {
        this.projectId = projectId;
    }

    public String getGeneralFormIds() {
        return generalFormIds;
    }

    public void setGeneralFormIds(String generalFormIds) {
        this.generalFormIds = generalFormIds;
    }

    public String getScheduleFormIds() {
        return scheduleFormIds;
    }

    public void setScheduleFormIds(String scheduleFormIds) {
        this.scheduleFormIds = scheduleFormIds;
    }

    public String getStagedFormIds() {
        return stagedFormIds;
    }

    public void setStagedFormIds(String stagedFormIds) {
        this.stagedFormIds = stagedFormIds;
    }

    public List<String> getGeneralformIdList() {
        Type type = new TypeToken<LinkedList<String>>() {
        }.getType();//todo use typeconvertor
        return new Gson().fromJson(generalFormIds, type);

    }

    public List<String> getScheduleFormIdList() {
        Type type = new TypeToken<LinkedList<String>>() {
        }.getType();//todo use typeconvertor
        return new Gson().fromJson(scheduleFormIds, type);

    }

    public List<String> getStagedformIdList() {
        Type type = new TypeToken<LinkedList<String>>() {
        }.getType();//todo use typeconvertor
        return new Gson().fromJson(stagedFormIds, type);

    }
}
