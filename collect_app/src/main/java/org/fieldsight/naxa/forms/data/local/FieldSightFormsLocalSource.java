package org.fieldsight.naxa.forms.data.local;

import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.common.BaseLocalDataSourceRX;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

public class FieldSightFormsLocalSource implements BaseLocalDataSourceRX<FieldSightFormDetails> {

    private static FieldSightFormsLocalSource INSTANCE;
    private FieldSightFormDetailDAO dao;

    private FieldSightFormsLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getFieldSightFormDAO();
    }

    public static FieldSightFormsLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormsLocalSource();
        }
        return INSTANCE;
    }

    public LiveData<List<FieldSightForm>> getGeneralForms(String projectId, String siteId) {
        return dao.getFormByType(Constant.FormType.GENERAl, projectId, siteId);
    }

    public LiveData<List<FieldSightForm>> getFormByType(String formType, String projectId, String siteId) {
        return dao.getFormByType(formType, projectId, siteId);
    }

    public LiveData<List<FieldSightForm>> getSurveyForms(String projectId, String siteId) {
        return dao.getFormByType(Constant.FormType.SURVEY, projectId, siteId);
    }

    public LiveData<List<FieldSightForm>> getScheduledForms(String projectId, String siteId) {
        return dao.getFormByType(Constant.FormType.SCHEDULE, projectId, siteId);
    }

    public LiveData<List<FieldSightForm>> getStagedForms(String projectId, String siteId) {
        return dao.getFormByType(Constant.FormType.STAGED, projectId, siteId);
    }

    public void saveForms(FieldSightFormDetails... fieldSightForm) {
        dao.insert(fieldSightForm);
    }

    @Override
    public LiveData<List<FieldSightFormDetails>> getAll() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Completable save(FieldSightFormDetails... items) {
        return Completable.fromAction(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<FieldSightFormDetails> items) {
        dao.insert(items);
    }


    @Override
    public void updateAll(ArrayList<FieldSightFormDetails> items) {

    }
}
