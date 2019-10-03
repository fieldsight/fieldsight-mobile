package org.fieldsight.naxa.v3.forms;

import com.google.gson.annotations.SerializedName;

import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.fieldsight.naxa.stages.data.Stage;
import org.fieldsight.naxa.survey.SurveyForm;

import java.util.ArrayList;

public class FieldSightFormResponse {

    @SerializedName("schedule")
    private ArrayList<ScheduleForm> scheduleForms;

    @SerializedName("general")
    private ArrayList<GeneralForm> generalForms;

    @SerializedName("stage")
    private ArrayList<Stage> stages;

    @SerializedName("survey")
    private ArrayList<SurveyForm> surveyForms;

    public ArrayList<ScheduleForm> getScheduleForms() {
        return scheduleForms;
    }

    public ArrayList<GeneralForm> getGeneralForms() {
        return generalForms;
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public ArrayList<SurveyForm> getSurveyForms() {
        return surveyForms;
    }
}
