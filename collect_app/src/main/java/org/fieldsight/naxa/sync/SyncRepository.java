package org.fieldsight.naxa.sync;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.onboarding.SyncableItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadStatus.PENDING;

@Deprecated
public class SyncRepository {

    private final SyncOLD syncOLD;
    public static SyncRepository instance;
    private final static String CHECKED = "checked";
    public final static String PROGRESS = "progress";
    private final static String DATE = "date";
    private final static String STATUS = "status";
    private final static String STATUS_ALL = "status_all";

    public SyncRepository(Application application) {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(application);
        this.syncOLD = database.getSyncDAO();
        init();
    }

    public synchronized static SyncRepository getInstance() {
        if (instance == null) {
            instance = new SyncRepository(Collect.getInstance());
        }

        return instance;
    }

    public void insert(SyncableItem... items) {
        new insertAsyncTask(syncOLD).execute(items);
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
        updateField(uid, DATE, false, formattedDate());
        hideProgress(uid);
    }


    public String formattedDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd, hh:mm aa", Locale.US);
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public Single<SyncableItem> getStatusById(int uid) {
        return syncOLD.getById(uid);
    }

    private void updateField(int uid, String code, boolean value, String stringValue) {
        Observable.just(uid)
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        switch (code) {
                            case CHECKED:
                                syncOLD.updateChecked(uid, value);
                                break;
                            case PROGRESS:
                                syncOLD.updateProgress(uid, value);
                                break;
                            case DATE:
                                syncOLD.updateDate(uid, stringValue);
                                break;
                            case STATUS:
                                syncOLD.updateStatus(uid, Integer.parseInt(stringValue));
                                break;
                            case STATUS_ALL:
                                syncOLD.setAllCheckedTrue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public LiveData<List<SyncableItem>> getAllSyncItems() {
        return syncOLD.getAllSyncableItems();
    }

    public void updateAllUnknown() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncOLD.updateAllUnknown(Constant.DownloadStatus.FAILED, false);
            }
        });

    }

    public void setAllRunningTaskAsFailed() {
        AsyncTask.execute(() -> {
            syncOLD.markAllRunningTaskAsFailed(formattedDate());
        });
    }

    private static class insertAsyncTask extends AsyncTask<SyncableItem, Void, Void> {

        private final SyncOLD syncOLD;

        insertAsyncTask(SyncOLD dao) {
            syncOLD = dao;
        }

        @Override
        protected Void doInBackground(final SyncableItem... items) {
            syncOLD.insert(items);
            return null;
        }
    }

    public void init() {
        SyncableItem[] syncableItems = new SyncableItem[]{
                new SyncableItem(Constant.DownloadUID.PROJECT_SITES, PENDING, null, "Project and sites", "Downloads your assigned PROJECT and sites"),
                new SyncableItem(Constant.DownloadUID.ALL_FORMS, PENDING, null, "Forms", "Downloads all FORMS for assigned sites"),
//                new SyncableItem(Constant.DownloadUID.ODK_FORMS, PENDING, null, "ODK FORMS", "Downloads odk FORMS for your sites"),
//                new SyncableItem(Constant.DownloadUID.GENERAL_FORMS, PENDING, null, "General FORMS", "Downloads general FORMS for your sites"),
//                new SyncableItem(Constant.DownloadUID.STAGED_FORMS, PENDING, null, "Staged FORMS", "Downloads scheduled FORMS for your sites"),
//                new SyncableItem(Constant.DownloadUID.SCHEDULED_FORMS, PENDING, null, "Scheduled FORMS", "Download scheduled FORMS for your sites"),
                new SyncableItem(Constant.DownloadUID.SITE_TYPES, PENDING, null, "Site type(s)", "Download site types to filter staged FORMS"),
                new SyncableItem(Constant.DownloadUID.EDU_MATERIALS, PENDING, null, "Educational Materials", "Download educational attached for form(s)"),
                new SyncableItem(Constant.DownloadUID.PROJECT_CONTACTS, PENDING, null, "Project Contact(s)", "Download contact information for people associated with your PROJECT"),
                new SyncableItem(Constant.DownloadUID.PREV_SUBMISSION, PENDING, null, "Previous Submissions", "Download previous submission(s) for FORMS"),
        };


        insert(syncableItems);
    }
}
