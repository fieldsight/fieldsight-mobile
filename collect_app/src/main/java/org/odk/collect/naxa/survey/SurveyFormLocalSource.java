package org.odk.collect.naxa.survey;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

public class SurveyFormLocalSource implements BaseLocalDataSource<SurveyForm> {

    private static SurveyFormLocalSource INSTANCE;
    private SurveyFormDAO dao;


    private SurveyFormLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSurveyDAO();
    }

    public static SurveyFormLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SurveyFormLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<SurveyForm>> getAll() {
        return dao.getAllSurveyForms();
    }

    @Override
    public void save(SurveyForm... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<SurveyForm> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<SurveyForm> items) {
        AsyncTask.execute(() -> dao.updateAll(items));

    }

    public LiveData<List<SurveyForm>> getByProjectId(String projectId) {
        return dao.getByProjectId(projectId);
    }
}
