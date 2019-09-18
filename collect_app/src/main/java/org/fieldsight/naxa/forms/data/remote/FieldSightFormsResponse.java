package org.fieldsight.naxa.forms.data.remote;

import com.google.gson.annotations.SerializedName;

import org.fieldsight.naxa.forms.data.local.FieldSightForm;

import java.util.ArrayList;

public class FieldSightFormsResponse {

    @SerializedName("schedule")
    private ArrayList<FieldSightForm> scheduleForms;

    @SerializedName("general")
    private ArrayList<FieldSightForm> generalForms;

    @SerializedName("stage")
    private ArrayList<FieldSightForm> stages;

    @SerializedName("survey")
    private ArrayList<FieldSightForm> surveyForms;

    public ArrayList<FieldSightForm> getScheduleForms() {
        return scheduleForms;
    }

    public ArrayList<FieldSightForm> getGeneralForms() {
        return generalForms;
    }

    public ArrayList<FieldSightForm> getStages() {
        return stages;
    }

    public ArrayList<FieldSightForm> getSurveyForms() {
        return surveyForms;
    }
}
