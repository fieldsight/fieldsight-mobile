package org.odk.collect.naxa.sync;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.onboarding.SyncableItems;

import java.util.List;

public class SyncRepository {

    private SyncDao syncDao;

    public SyncRepository(Application application) {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(application);
        this.syncDao = database.getSyncDAO();
        init();
    }

    public void init() {

        SyncableItems[] syncableItems = new SyncableItems[]{
                new SyncableItems(Constant.DownloadUID.PROJECT_SITES,
                        Constant.DownloadStatus.PENDING, null, "Project and Sites", "Downloads your assigned project and sites"),
                new SyncableItems(Constant.DownloadUID.ODK_FORMS,
                        Constant.DownloadStatus.PENDING, null, "ODK Forms", "Downloads odk forms for your sites"),
                new SyncableItems(Constant.DownloadUID.GENERAL_FORMS,
                        Constant.DownloadStatus.PENDING, null, "General Forms", "Downloads general forms for your sites"),
                new SyncableItems(Constant.DownloadUID.PROJECT_CONTACTS,
                        Constant.DownloadStatus.PENDING, null, "Project Contacts", "Downloads contact information for personale in your project")
          };


        insert(syncableItems);
    }

    public void insert(SyncableItems... items) {
        new insertAsyncTask(syncDao).execute(items);
    }


    public LiveData<List<SyncableItems>> getAllSyncItems(){
        return syncDao.getAllSyncableItems();
    }

    private static class insertAsyncTask extends AsyncTask<SyncableItems, Void, Void> {

        private SyncDao syncDao;

        insertAsyncTask(SyncDao dao) {
            syncDao = dao;
        }

        @Override
        protected Void doInBackground(final SyncableItems... items) {
            syncDao.insert(items);
            return null;
        }
    }
}
