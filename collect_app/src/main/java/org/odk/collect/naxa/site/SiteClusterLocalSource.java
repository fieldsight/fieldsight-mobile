package org.odk.collect.naxa.site;

import android.arch.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.site.data.SiteCluster;

import java.util.ArrayList;
import java.util.List;

public class SiteClusterLocalSource implements BaseLocalDataSource<SiteCluster> {
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
    public LiveData<List<SiteCluster>> getAll() {
        return null;
    }

    public LiveData<List<SiteCluster>> getByProjectId(String projectId) {
        return dao.getByProjectId(projectId);
    }

    @Override
    public void save(SiteCluster... items) {

    }

    @Override
    public void save(ArrayList<SiteCluster> items) {

    }

    @Override
    public void updateAll(ArrayList<SiteCluster> items) {

    }
}
