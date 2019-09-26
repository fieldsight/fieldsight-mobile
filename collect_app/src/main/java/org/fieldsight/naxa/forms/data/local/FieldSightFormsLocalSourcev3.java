package org.fieldsight.naxa.forms.data.local;

import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.common.BaseLocalDataSourceRX;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

public class FieldSightFormsLocalSourcev3 implements BaseLocalDataSourceRX<FieldsightFormDetailsv3> {

    private static FieldSightFormsLocalSourcev3 INSTANCE;
    private FieldSightFormDetailDAOV3 dao;

    private FieldSightFormsLocalSourcev3() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getFieldSightFOrmDAOV3();
    }

    public static FieldSightFormsLocalSourcev3 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormsLocalSourcev3();
        }
        return INSTANCE;
    }

    public LiveData<List<FieldsightFormDetailsv3>> getFormByType(String formType, String projectId, String siteId) {
        return dao.getFormByType(formType, projectId, siteId);
    }


    public void saveForms(FieldsightFormDetailsv3... fieldSightForm) {
        dao.insert(fieldSightForm);
    }

    @Override
    public LiveData<List<FieldsightFormDetailsv3>> getAll() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Completable save(FieldsightFormDetailsv3... items) {
        return Completable.fromAction(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<FieldsightFormDetailsv3> items) {
        dao.insert(items);
    }


    @Override
    public void updateAll(ArrayList<FieldsightFormDetailsv3> items) {

    }

    public List<String> getEducationMaterial(String projectId) {
        return dao.getEducationMaterailByProjectIds(projectId);
    }
}
