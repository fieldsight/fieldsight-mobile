package org.odk.collect.naxa.site;

import android.arch.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

public class SiteTypeLocalSource implements BaseLocalDataSource<SiteType> {

    private static SiteTypeLocalSource INSTANCE;
    private SiteTypeDAO dao;


    private SiteTypeLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSiteTypesDAO();
    }


    public static SiteTypeLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteTypeLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<SiteType>> getAll() {
        return null;
    }

    @Override
    public void save(SiteType... items) {
        dao.insert(items);
    }

    @Override
    public void save(ArrayList<SiteType> items) {
        dao.insert(items);
    }

    @Override
    public void updateAll(ArrayList<SiteType> items) {
        dao.updateAll(items);
    }
}
