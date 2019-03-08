package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.database.FieldSightConfigDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Single;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDITED_SITES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.ODK_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.OFFLINE_SITES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.PROJECT_SITES;

public class SyncLocalSource implements BaseLocalDataSourceRX<Sync> {

    private static SyncLocalSource INSTANCE;
    private SyncDAO syncDAO;

    public static SyncLocalSource getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new SyncLocalSource();
        }

        return INSTANCE;
    }


    private SyncLocalSource() {

        FieldSightConfigDatabase database = FieldSightConfigDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.syncDAO = database.getSyncDao();
    }


    @Override
    public LiveData<List<Sync>> getAll() {
        return syncDAO.getAll();
    }

    @Override
    public Completable save(Sync... items) {
        return Completable.fromAction(() -> {
            syncDAO.insertOrIgnore(items);
        });
    }


    @Override
    public Completable save(ArrayList<Sync> items) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void saveAsAsync(Sync... items) {
        AsyncTask.execute(() -> {
            syncDAO.insertOrIgnore(items);
        });
    }

    @Override
    public void updateAll(ArrayList<Sync> items) {

    }

    Completable toggleAllChecked() {

        return syncDAO.selectedItemsCount()
                .toObservable()
                .flatMapCompletable(integer -> Completable.fromAction(() -> {
                    if (integer > 0) {
                        syncDAO.markAllAsUnChecked();
                    } else {
                        syncDAO.markAllAsChecked();
                    }

                }));
    }

    public Single<Integer> selectedItemCount() {
        return syncDAO.selectedItemsCount();
    }

    LiveData<Integer> selectedItemCountLive() {
        return syncDAO.selectedItemsCountLive();
    }

    LiveData<Integer> runningItemCountLive() {
        return syncDAO.runningItemCountLive(Constant.DownloadStatus.RUNNING);
    }

    Completable toggleSingleItem(Sync sync) {
        return Completable.fromAction(() -> {
            if (sync.isChecked()) {
                syncDAO.markAsUnchecked(sync.getUid());
            } else {
                syncDAO.markAsChecked(sync.getUid());
            }
        });
    }

    Single<List<Sync>> getAllChecked() {
        return syncDAO.getAllChecked();
    }


    public void markAsRunning(int uid) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncDAO.markSelectedAsRunning(uid, Constant.DownloadStatus.RUNNING);
                clearErrorMessage(uid);
            }
        });

    }



    public void markAsFailed(int uid) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                syncDAO.markSelectedAsFailed(uid, Constant.DownloadStatus.FAILED, formattedDate());
            }
        });

    }

    void markAsDisabled(int uid,String message) {
        AsyncTask.execute(() -> syncDAO.markSelectedAsDisabled(uid, Constant.DownloadStatus.DISABLED, formattedDate(), message));

    }


    public void markAsFailed(int uid, String message) {
        AsyncTask.execute(() -> syncDAO.markFailedWithMsg(uid, Constant.DownloadStatus.FAILED, formattedDate(), message));

    }

    private void updateErrorMessage(int uid, String errorMessage) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncDAO.updateErrorMessage(uid, errorMessage);
            }
        });
    }

    public void addErrorMessage(int uid, String errorMessage) {
        updateErrorMessage(uid, errorMessage);
    }


    public void clearErrorMessage(int uid) {
        updateErrorMessage(uid, "");
    }


    public void markAsPending(int uid) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncDAO.markSelectedAsRunning(uid, Constant.DownloadStatus.PENDING);
            }
        });

    }

    public void markAsPending(int uid, String message) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncDAO.markSelectedAsDisabled(uid, Constant.DownloadStatus.PENDING,  formattedDate(), message);
            }
        });

    }


    public void markAllAsPending() {
        AsyncTask.execute(() -> {
            syncDAO.markAllAsPending(PENDING);
        });
    }

    public void markAsCompleted(int uid) {
        AsyncTask.execute(() -> {
            syncDAO.markSelectedAsCompleted(uid, Constant.DownloadStatus.COMPLETED, formattedDate());
            clearErrorMessage(uid);
        });
    }

    public void updateProgress(int uid, int total, int progress) {
        AsyncTask.execute(() -> syncDAO.updateProgress(uid, total, progress));
    }

    private String formattedDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd, hh:mm aa", Locale.US);
        String formattedDate = df.format(date);
        return formattedDate;
    }


    private Sync[] getData() {

        return new Sync[]{
                new Sync(PROJECT_SITES, PENDING, "Project and sites", "Downloads your assigned project and sites"),
                new Sync(Constant.DownloadUID.ALL_FORMS, PENDING, "Forms", "Downloads all forms for assigned sites"),
                new Sync(Constant.DownloadUID.SITE_TYPES, PENDING, "Site type(s)", "Download site types to filter staged forms"),
                new Sync(Constant.DownloadUID.EDU_MATERIALS, PENDING, "Educational Materials", "Download educational attached for form(s)"),
                new Sync(Constant.DownloadUID.PROJECT_CONTACTS, PENDING, "Project Contact(s)", "Download contact information for people associated with your project"),
                new Sync(Constant.DownloadUID.PREV_SUBMISSION, PENDING, "Previous Submissions", "Download previous submission(s) for forms"),
                new Sync(EDITED_SITES, PENDING, "Edited Site(s)", ""),
                new Sync(OFFLINE_SITES, PENDING, "Offline Site(s)", ""),
                new Sync(ODK_FORMS, PENDING, "TEST ODK", ""),


        };
    }

    public Completable init() {
        return save(getData());
    }

    public void deleteById(int uid) {
        AsyncTask.execute(() -> {
            syncDAO.deleteById(uid);
        });
    }


    public void setAllRunningTaskAsFailed() {
        AsyncTask.execute(() -> {
            syncDAO.setAllRunningTaskAsFailed(formattedDate());
        });
    }
}
