package org.bcss.collect.naxa.sync;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.onboarding.SyncableItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;


public class SyncRepository {

    private SyncDao syncDao;
    public static SyncRepository INSTANCE;
    private final String CHECKED = "checked";
    public final String PROGRESS = "progress";
    private final String DATE = "date";
    private final String STATUS = "status";
    private final String STATUS_ALL = "status_all";

    public SyncRepository(Application application) {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(application);
        this.syncDao = database.getSyncDAO();
        init();
    }

    public static SyncRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SyncRepository(Collect.getInstance());
        }

        return INSTANCE;
    }

    public void insert(SyncableItem... items) {
        new insertAsyncTask(syncDao).execute(items);
    }

    public void setChecked(int uid, boolean bool) {
        updateField(uid, CHECKED, bool, null);
    }

    public void showProgress(int uid) {
        updateField(uid, PROGRESS, true, null);
    }

    public void setAllCheckedTrue() {
        updateField(0, STATUS_ALL, true, null);
    }

    public void updateStatus(int uid, int status) {
        updateField(uid, STATUS, false, String.valueOf(status));
    }

    public void setSuccess(int uid) {
        hideProgress(uid);
        updateDate(uid);
        updateStatus(uid, Constant.DownloadStatus.COMPLETED);
    }

    public void setError(int uid) {
        hideProgress(uid);
        updateDate(uid);
        updateStatus(uid, Constant.DownloadStatus.FAILED);
    }

    private void hideProgress(int uid) {
        updateField(uid, PROGRESS, false, null);
    }

    private void updateDate(int uid) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd, hh:mm aa", Locale.US);
        String formattedDate = df.format(date);
        updateField(uid, DATE, false, formattedDate);
        hideProgress(uid);
    }

    public Single<SyncableItem> getStatusById(int uid) {
        return syncDao.getById(uid);
    }

    private void updateField(int uid, String code, boolean value, String stringValue) {
        Observable.just(uid)
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        switch (code) {
                            case CHECKED:
                                syncDao.updateChecked(uid, value);
                                break;
                            case PROGRESS:
                                syncDao.updateProgress(uid, value);
                                break;
                            case DATE:
                                syncDao.updateDate(uid, stringValue);
                                break;
                            case STATUS:
                                syncDao.updateStatus(uid, Integer.parseInt(stringValue));
                                break;
                            case STATUS_ALL:
                                syncDao.setAllCheckedTrue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public LiveData<List<SyncableItem>> getAllSyncItems() {
        return syncDao.getAllSyncableItems();
    }

    private static class insertAsyncTask extends AsyncTask<SyncableItem, Void, Void> {

        private SyncDao syncDao;

        insertAsyncTask(SyncDao dao) {
            syncDao = dao;
        }

        @Override
        protected Void doInBackground(final SyncableItem... items) {
            syncDao.insert(items);
            return null;
        }
    }

    public void init() {
        SyncableItem[] syncableItems = new SyncableItem[]{
                new SyncableItem(Constant.DownloadUID.PROJECT_SITES, PENDING, null, "Project and sites", "Downloads your assigned project and sites"),
                new SyncableItem(Constant.DownloadUID.ALL_FORMS, PENDING, null, "Forms", "Downloads all forms for assigned sites"),
//                new SyncableItem(Constant.DownloadUID.ODK_FORMS, PENDING, null, "ODK forms", "Downloads odk forms for your sites"),
//                new SyncableItem(Constant.DownloadUID.GENERAL_FORMS, PENDING, null, "General forms", "Downloads general forms for your sites"),
//                new SyncableItem(Constant.DownloadUID.STAGED_FORMS, PENDING, null, "Staged forms", "Downloads scheduled forms for your sites"),
//                new SyncableItem(Constant.DownloadUID.SCHEDULED_FORMS, PENDING, null, "Scheduled forms", "Download scheduled forms for your sites"),
                new SyncableItem(Constant.DownloadUID.SITE_TYPES, PENDING, null, "Site type(s)", "Download site types to filter staged forms"),
                new SyncableItem(Constant.DownloadUID.EDU_MATERIALS, PENDING, null, "Educational Materials", "Download educational attached for form(s)"),
                new SyncableItem(Constant.DownloadUID.PROJECT_CONTACTS, PENDING, null, "Project Contact(s)", "Download contact information for people associated with your project"),
                new SyncableItem(Constant.DownloadUID.PREV_SUBMISSION, PENDING, null, "Previous Submissions", "Download previous submission(s) for forms"),
        };


        insert(syncableItems);
    }
}
