package org.fieldsight.naxa.educational;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.generalforms.data.Em;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class EducationalMaterialsLocalSource implements BaseLocalDataSource<Em> {

    private static EducationalMaterialsLocalSource educationalMaterialsLocalSource;
    private final EducationalMaterialsDao dao;

    public static EducationalMaterialsLocalSource getInstance() {
        if (educationalMaterialsLocalSource == null) {
            educationalMaterialsLocalSource = new EducationalMaterialsLocalSource();
        }
        return educationalMaterialsLocalSource;
    }

    private EducationalMaterialsLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getEducationalMaterialDAO();
    }


    @Override
    public LiveData<List<Em>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(Em... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<Em> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Em> items) {

    }

    public Single<Em> getByFsFormId(String fsFormId) {
        return dao.getByFsFormId(fsFormId);

    }
}
