package org.fieldsight.naxa.sync;


import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSourceRX;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.database.FieldSightConfigDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Single;

import static org.fieldsight.naxa.common.Constant.DownloadStatus.COMPLETED;
import static org.fieldsight.naxa.common.Constant.DownloadStatus.DISABLED;
import static org.fieldsight.naxa.common.Constant.DownloadStatus.PENDING;
import static org.fieldsight.naxa.common.Constant.DownloadUID.EDITED_SITES;
import static org.fieldsight.naxa.common.Constant.DownloadUID.ODK_FORMS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.OFFLINE_SITES;
import static org.fieldsight.naxa.common.Constant.DownloadUID.PROJECT_SITES;

public class DownloadableItemLocalSource implements BaseLocalDataSourceRX<DownloadableItem> {

    private static DownloadableItemLocalSource downloadableItemLocalSource;
    private final DownloadableItemDAO syncDAO;

    public synchronized static DownloadableItemLocalSource getDownloadableItemLocalSource() {
        if (downloadableItemLocalSource == null) {
            downloadableItemLocalSource = new DownloadableItemLocalSource();
        }

        return downloadableItemLocalSource;
    }


    private DownloadableItemLocalSource() {

        FieldSightConfigDatabase database = FieldSightConfigDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.syncDAO = database.getSyncDao();
    }


    @Override
    public LiveData<List<DownloadableItem>> getAll() {
        return syncDAO.getAll();
    }

    @Override
    public Completable save(DownloadableItem... items) {
        return Completable.fromAction(() -> {
            syncDAO.insertOrIgnore(items);
        });
    }

    @Override
    public void save(ArrayList<DownloadableItem> items) {
        throw new RuntimeException("Not implemented yet");
    }


    @Override
    public void updateAll(ArrayList<DownloadableItem> items) {

    }

    public Completable toggleAllChecked() {

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

    public LiveData<Integer> selectedItemCountLive() {
        return syncDAO.selectedItemsCountLive();
    }

    public LiveData<Integer> runningItemCountLive() {
        return syncDAO.runningItemCountLive(Constant.DownloadStatus.RUNNING);
    }

    public Completable toggleSingleItem(DownloadableItem downloadableItem) {
        return Completable.fromAction(() -> {
            if (downloadableItem.isChecked()) {
                syncDAO.markAsUnchecked(downloadableItem.getUid());
            } else {
                syncDAO.markAsChecked(downloadableItem.getUid());
            }
        });
    }

    public Single<List<DownloadableItem>> getAllChecked() {
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

    public void markAsRunning(int uid, String message) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncDAO.markSelectedAsRunning(uid, message);
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

    public void markAsDisabled(int uid, String message) {
        AsyncTask.execute(() -> syncDAO.markSelectedAsDisabled(uid, DISABLED, formattedDate(), message));

    }

    Completable updateProgress(int current, int total) {
        return Completable.fromAction(() -> syncDAO.updateProgress(ODK_FORMS, total, current));
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
                syncDAO.markSelectedAsRunning(uid,  PENDING);
            }
        });

    }

    public void markAsPending(int uid, String message) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                syncDAO.markSelectedAsDisabled(uid,  PENDING, formattedDate(), message);
            }
        });

    }


    public void markAllAsPending() {
        AsyncTask.execute(() -> {
            syncDAO.markAllAsPending(PENDING,  DISABLED);
        });
    }

    public void markAsCompleted(int uid) {
        AsyncTask.execute(() -> {
            syncDAO.markSelectedAsCompleted(uid,  COMPLETED, formattedDate());
            clearErrorMessage(uid);
        });
    }

    public void updateProgress(int uid, int total, int progress) {
        AsyncTask.execute(() -> syncDAO.updateProgress(uid, total, progress));
    }

    private String formattedDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd, hh:mm aa", Locale.US);
        return df.format(date);
    }


    private DownloadableItem[] getData() {

        return new DownloadableItem[]{
                new DownloadableItem(PROJECT_SITES, PENDING, "Project and sites", "Downloads your assigned PROJECT and sites"),
                new DownloadableItem(Constant.DownloadUID.ALL_FORMS, PENDING, "Forms", "Downloads all FORMS for assigned sites"),
                new DownloadableItem(Constant.DownloadUID.SITE_TYPES, PENDING, "Site type(s)", "Download site types to filter staged FORMS"),
                new DownloadableItem(Constant.DownloadUID.EDU_MATERIALS, PENDING, "Educational Materials", "Download educational attached for form(s)"),
                new DownloadableItem(Constant.DownloadUID.PROJECT_CONTACTS, PENDING, "Project Contact(s)", "Download contact information for people associated with your PROJECT"),
                //new DownloadableItem(Constant.DownloadUID.PREV_SUBMISSION, PENDING, "Previous Submissions", "Download previous submission(s) for FORMS"),
                new DownloadableItem(EDITED_SITES, PENDING, "Edited Site(s)", ""),
                new DownloadableItem(OFFLINE_SITES, PENDING, "Offline Site(s)", ""),
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


    void setAllRunningTaskAsFailed() {
        AsyncTask.execute(() -> {
            syncDAO.setAllRunningTaskAsFailed(formattedDate());
        });
    }

    public void setProgress(int uid, int current, int total) {
        AsyncTask.execute(() -> syncDAO.setProgress(uid, current, total));
    }

    public Single<DownloadableItem> getStatusById(int projectSites) {
        return syncDAO.getStatusById(projectSites);
    }

    public void markAsUnchecked(int uid) {
        AsyncTask.execute(() -> syncDAO.markAsUnchecked(uid));
    }

    public void markAllCheckedAsUnchecked() {
        AsyncTask.execute(() -> syncDAO.markAllAsUnChecked());

    }
}
