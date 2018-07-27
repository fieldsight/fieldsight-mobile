package org.odk.collect.naxa.common;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.database.FieldSightConfigDatabase;
import org.odk.collect.naxa.common.database.SiteOveride;
import org.odk.collect.naxa.common.database.SiteOverideDAO;
import org.odk.collect.naxa.site.db.SiteDao;

import java.util.ArrayList;
import java.util.List;

public class SiteOverideLocalSource implements BaseLocalDataSource<SiteOveride> {

    private static SiteOverideLocalSource INSTANCE;
    private SiteOverideDAO dao;
    private SiteDao siteDao;

    private SiteOverideLocalSource() {
        FieldSightConfigDatabase database = FieldSightConfigDatabase.getDatabase(Collect.getInstance());//todo inject context
        FieldSightDatabase fieldSightDatabase = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSiteOverideDAO();
        this.siteDao = fieldSightDatabase.getSiteDAO();
    }

    public static SiteOverideLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteOverideLocalSource();
        }
        return INSTANCE;
    }



    @Override
    public LiveData<List<SiteOveride>> getAll() {
        return null;
    }

    @Override
    public void save(SiteOveride... items) {

        AsyncTask.execute(() -> {


            for (SiteOveride siteOveride : items) {

                for (String siteId : siteOveride.getGeneralformIdList()) {
                    siteDao.updateGeneralFormDeployedFrom(siteId, Constant.FormDeploymentFrom.SITE);
                }

                for (String siteId : siteOveride.getScheduleFormIdList()) {
                    siteDao.updateScheduleFormDeployedFrom(siteId, Constant.FormDeploymentFrom.SITE);
                }


                for (String siteId : siteOveride.getStagedformIdList()) {
                    siteDao.updateStagedFormDeployedFrom(siteId, Constant.FormDeploymentFrom.SITE);
                }

            }

            dao.insert(items);
        });


    }

    @Override
    public void save(ArrayList<SiteOveride> items) {

    }

    @Override
    public void updateAll(ArrayList<SiteOveride> items) {

    }
}
