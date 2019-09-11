package org.fieldsight.naxa.forms.source.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class FormSchedule {
    @SerializedName("date_range_start")
    private String startDate;

    @SerializedName("date_range_end")
    private String endDate;

    @SerializedName("selected_days")
    private ArrayList<String> frequency;

    @SerializedName("type")
    @Expose
    private String type;


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getFrequency() {
        return frequency;
    }

    public void setFrequency(ArrayList<String> frequency) {
        this.frequency = frequency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
