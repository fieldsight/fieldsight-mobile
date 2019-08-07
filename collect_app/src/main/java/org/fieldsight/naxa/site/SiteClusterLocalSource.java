package org.fieldsight.naxa.site;

import androidx.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.site.data.SiteRegion;

import java.util.ArrayList;
import java.util.List;

public class SiteClusterLocalSource implements BaseLocalDataSource<SiteRegion> {
    private static SiteClusterLocalSource INSTANCE;
    private SiteClusterDAO dao;


    private SiteClusterLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSiteClusterDAO();
    }


    public static SiteClusterLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteClusterLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<SiteRegion>> getAll() {
        return null;
    }

    public LiveData<List<SiteRegion>> getByProjectId(String projectId) {
        return dao.getByProjectId(projectId);
    }

    @Override
    public void save(SiteRegion... items) {

    }

    @Override
    public void save(ArrayList<SiteRegion> items) {

    }

    @Override
    public void updateAll(ArrayList<SiteRegion> items) {

    }
}
