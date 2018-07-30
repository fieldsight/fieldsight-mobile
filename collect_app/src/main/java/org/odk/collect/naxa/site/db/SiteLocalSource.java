package org.odk.collect.naxa.site.db;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.List;

import static org.odk.collect.naxa.common.Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED;
import static org.odk.collect.naxa.common.Constant.SiteStatus.IS_UNVERIFIED_SITE;

public class SiteLocalSource implements BaseLocalDataSource<Site> {


    private static SiteLocalSource INSTANCE;
    private SiteDao dao;


    private SiteLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSiteDAO();
    }


    public static SiteLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteLocalSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<Site>> getAll() {
        //not implemented
        return null;
    }

    public LiveData<List<Site>> getById(String projectId) {
        return dao.getSiteByProjectId(projectId);
    }


    public List<Site> searchSites(String searchQuery) {
        return dao.searchSites(searchQuery);
    }

    @Override
    public void save(Site... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<Site> items) {
        //AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Site> items) {

    }
}
