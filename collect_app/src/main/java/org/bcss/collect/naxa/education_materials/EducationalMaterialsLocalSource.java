package org.bcss.collect.naxa.education_materials;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

public class EducationalMaterialsLocalSource implements BaseLocalDataSource<EducationalMaterial> {

    private static EducationalMaterialsLocalSource INSTANCE;
    private final EducationalMaterialsDao dao;

    public static EducationalMaterialsLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EducationalMaterialsLocalSource();
        }
        return INSTANCE;
    }

    private EducationalMaterialsLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getEducationalMaterialDAO();
    }


    @Override
    public LiveData<List<EducationalMaterial>> getAll() {
        return null;
    }

    @Override
    public void save(EducationalMaterial... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<EducationalMaterial> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<EducationalMaterial> items) {

    }
}
