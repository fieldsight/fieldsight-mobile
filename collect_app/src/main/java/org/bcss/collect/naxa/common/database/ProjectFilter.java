package org.bcss.collect.naxa.common.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ProjectFilter {
    @PrimaryKey
    @NonNull
    private String id;

    private String selectedRegionId;
    private String selectedRegionLabel;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSelectedRegionId() {
        return selectedRegionId;
    }

    public void setSelectedRegionId(String selectedRegionId) {
        this.selectedRegionId = selectedRegionId;
    }

    public String getSelectedRegionLabel() {
        return selectedRegionLabel;
    }

    public void setSelectedRegionLabel(String selectedRegionLabel) {
        this.selectedRegionLabel = selectedRegionLabel;
    }

    public ProjectFilter(){


    }

    @Ignore
    public ProjectFilter(@NonNull String id, String selectedRegionId, String selectedRegionLabel) {
        this.id = id;
        this.selectedRegionId = selectedRegionId;
        this.selectedRegionLabel = selectedRegionLabel;
    }


}
