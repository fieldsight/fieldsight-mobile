package org.bcss.collect.naxa.site;

import android.arch.lifecycle.LiveData;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.site.data.SiteRegion;

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
