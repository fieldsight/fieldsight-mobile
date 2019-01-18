package org.bcss.collect.naxa.onboarding;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.listeners.DownloadFormsTaskListener;
import org.bcss.collect.android.listeners.FormListDownloaderListener;
import org.bcss.collect.android.logic.FormDetails;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.exception.DownloadRunningException;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;
import org.bcss.collect.naxa.task.FieldSightDownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.Constant.EXTRA_RECEIVER;
import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_AUTH_REQUIRED;
import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_ERROR_MSG;

/**
 * Created on 11/18/17
 * by nishon.tan@gmail.com
 */


@SuppressLint("LogNotTimber")
public class XMLFormDownloadService extends IntentService implements DownloadFormsTaskListener, FormListDownloaderListener {


    private static final String FORMNAME = "formname";
    private static final String FORMDETAIL_KEY = "formdetailkey";
    private static final String FORMID_DISPLAY = "formiddisplay";
    private static final String FORM_ID_KEY = "formid";
    private static final String FORM_VERSION_KEY = "formversion";

    private static final String TAG = XMLFormDownloadService.class.getName();

    private FieldSightDownloadFormListTask mDownloadFormListTask;
    private HashMap<String, FormDetails> mFormNamesAndURLs = new HashMap<String, FormDetails>();
    private ArrayList<HashMap<String, String>> mFormList;
    private LinkedList<XMLForm> formsToDownlaod;
    private Bundle message;

    private ResultReceiver receiver;
    private DownloadProgress downloadProgress;


    public static void start(Context context, @NonNull XMLFormDownloadReceiver receiver) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, XMLFormDownloadService.class);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

    public XMLFormDownloadService() {
        super(XMLFormDownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            ToastUtils.showLongToast("Failed to start form download task");
            stopSelf();
            return;
        }

        formsToDownlaod = new LinkedList<>();
        message = new Bundle();
        receiver = intent.getParcelableExtra(EXTRA_RECEIVER);

        SyncRepository.getInstance()
                .getStatusById(Constant.DownloadUID.PROJECT_SITES)
                .map(syncableItem -> {
                    if (syncableItem.isProgressStatus()) {
                        throw new DownloadRunningException("Waiting until project and sites are downloaded");

                    }
                    return syncableItem;
                })
                .toObservable()
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) {
                                if (throwable instanceof DownloadRunningException) {
                                    Timber.i("Polling for project sites");
                                    return Observable.timer(3, TimeUnit.SECONDS);
                                }

                                return Observable.just(throwable);
                            }
                        });

                    }
                })
                .flatMapSingle(new Function<SyncableItem, SingleSource<List<Project>>>() {
                    @Override
                    public SingleSource<List<Project>> apply(SyncableItem syncableItem) {
                        return ProjectLocalSource.getInstance()
                                .getProjectsMaybe();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Project>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.i("Subscribed");
                    }

                    @Override
                    public void onNext(List<Project> projects) {
                        Timber.i("onNext %d", projects.size());

                        ArrayList<String> projectIds = new ArrayList<>();

                        for (Project project : projects) {
                            projectIds.add(project.getId());
                        }

                        for (String projectId : projectIds) {
                            XMLForm XMLForm;

                            String baseurl = FieldSightUserSession.getServerUrl(Collect.getInstance());
                            XMLForm = new XMLFormBuilder()
                                    .setFormCreatorsId(projectId)
                                    .setIsCreatedFromProject(false)
                                    .setDownloadUrl(baseurl + APIEndpoint.ASSIGNED_FORM_LIST_SITE.concat(projectId))
                                    .createXMLForm();

                            formsToDownlaod.add(XMLForm);

                            XMLForm = new XMLFormBuilder()
                                    .setFormCreatorsId(projectId)
                                    .setIsCreatedFromProject(true)
                                    .setDownloadUrl(baseurl + APIEndpoint.ASSIGNED_FORM_LIST_PROJECT.concat(projectId))
                                    .createXMLForm();
                            formsToDownlaod.add(XMLForm);

                        }

                        if (formsToDownlaod == null || formsToDownlaod.isEmpty()) {
                            broadcastDownloadError("No project id provided to download forms");
                        } else {
                            downloadFormList(getApplicationContext(), XMLFormDownloadService.this, XMLFormDownloadService.this, formsToDownlaod.get(0));
                            broadcastDownloadStarted();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.i("onError");
                        e.printStackTrace();
                        broadcastDownloadError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("onComplete ");
                    }
                });


    }


    public void cancelTask() {
        mDownloadFormListTask.setDownloaderListener(null);
        mDownloadFormListTask.cancel(true);
        mDownloadFormListTask = null;
    }


    public void downloadFormList(Context context, FormListDownloaderListener fl, DownloadFormsTaskListener fdl, XMLForm xmlForm) {

        mFormNamesAndURLs = new HashMap<String, FormDetails>();
        if (mDownloadFormListTask != null && mDownloadFormListTask.getStatus() != AsyncTask.Status.FINISHED) {
            return; // we are already doing the download!!!
        } else if (mDownloadFormListTask != null) {
            mDownloadFormListTask.setDownloaderListener(null);
            mDownloadFormListTask.cancel(true);
            mDownloadFormListTask = null;
            mDownloadFormListTask = null;
        }

        Timber.d("Hitting URL %s ", xmlForm.getDownloadUrl());
        mDownloadFormListTask = new FieldSightDownloadFormListTask(xmlForm);
        mDownloadFormListTask.setDownloaderListener(fl);
        mDownloadFormListTask.execute();
    }


    @Override
    public void formsDownloadingComplete(HashMap<FormDetails, String> result) {
        if (result.containsKey(DL_ERROR_MSG)) {
            Log.e(TAG, " Forms could not be downloaded ");
            broadcastDownloadError("Forms could not be downloaded");
            return;
        }

        if (result.containsKey(DL_AUTH_REQUIRED)) {
            Log.e(TAG, " Mismatched token");
            broadcastDownloadError("Forms could not be downloaded");
            return;
        }

        Log.d(TAG, "Forms Downloading Complete for project " + formsToDownlaod.get(0).getFormCreatorsId());


        //remove the site that has completed download
        formsToDownlaod.remove(0);
        if (formsToDownlaod.size() > 0) {
            downloadFormList(getApplicationContext(), this, this, formsToDownlaod.get(0));
            Log.d(TAG, " Forms downloading for site " + formsToDownlaod.get(0));
        } else {
            Log.d(TAG, " All forms downloading complete ");
            broadcastCompletedDownload();
        }
    }

    private void broadcastCompletedDownload() {
        receiver.send(DownloadProgress.STATUS_FINISHED_FORM, Bundle.EMPTY);
    }

    private void broadcastDownloadError(String error) {
        message.putString(EXTRA_MESSAGE, error);
        receiver.send(DownloadProgress.STATUS_ERROR, message);
    }

    private void broadcastDownloadStarted() {

        receiver.send(DownloadProgress.STATUS_RUNNING, Bundle.EMPTY);
    }

    private void broadcastDownloadProgress(String currentFile, int progress, int total) {

        DownloadProgress downloadProgress = new DownloadProgress(currentFile, progress, total, currentFile, false);

        message.putSerializable(EXTRA_OBJECT, downloadProgress);
        receiver.send(DownloadProgress.STATUS_PROGRESS_UPDATE, message);
    }


    @Override
    public void progressUpdate(String currentFile, int progress, int total) {
        broadcastDownloadProgress(currentFile, progress, total);
    }

    @Override
    public void formsDownloadingCancelled() {

    }

    @Override
    public void formListDownloadingComplete(HashMap<String, FormDetails> result) {
        if (result.containsKey(DL_AUTH_REQUIRED)) {
            // need authorization
            //this should never happen
            broadcastDownloadError("Bad Token");
            return;
        } else if (result.containsKey(DL_ERROR_MSG)) {
            broadcastDownloadError("Form list could not be downloaded");
            return;
        } else {

            Log.d(TAG, "Form List Downloading complete");

            // Everything worked. Clear the list and add the results.
            mFormNamesAndURLs = result;
            //array list added here siteName on Create
            mFormList = new ArrayList<HashMap<String, String>>();

            mFormList.clear();

            ArrayList<String> ids = new ArrayList<String>(mFormNamesAndURLs.keySet());
            for (int i = 0; i < result.size(); i++) {
                String formDetailsKey = ids.get(i);
                FormDetails details = mFormNamesAndURLs.get(formDetailsKey);
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(FORMNAME, details.getFormName());
                item.put(FORMID_DISPLAY,
                        ((details.getFormVersion() == null) ? "" : (getString(R.string.version) + " " + details.getFormVersion() + " ")) +
                                "ID: " + details.getFormID());
                item.put(FORMDETAIL_KEY, formDetailsKey);
                item.put(FORM_ID_KEY, details.getFormID());
                item.put(FORM_VERSION_KEY, details.getFormVersion());

                // Insert the new form in alphabetical order.
                if (mFormList.size() == 0) {
                    mFormList.add(item);
                } else {
                    int j;
                    for (j = 0; j < mFormList.size(); j++) {
                        HashMap<String, String> compareMe = mFormList.get(j);
                        String name = compareMe.get(FORMNAME);
                        if (name.compareTo(mFormNamesAndURLs.get(ids.get(i)).getFormName()) > 0) {
                            break;
                        }
                    }
                    mFormList.add(j, item);
                }
            }

            //everything worked now download all files
            downloadAllFiles();

        }


    }


    public void downloadAllFiles() {
        int totalCount;
        ArrayList<FormDetails> filesToDownload = new ArrayList<FormDetails>();

        filesToDownload.addAll(mFormNamesAndURLs.values());

        totalCount = filesToDownload.size();

        Log.d(TAG, "Total number of forms being downloaded: " + totalCount);


        if (totalCount > 0) {

            // show dialog box
            // showRefreshing(PROGRESS_DIALOG);
            DownloadFormsTask mDownloadFormsTask = new DownloadFormsTask();
            mDownloadFormsTask.setDownloaderListener(this);
            mDownloadFormsTask.execute(filesToDownload);
        } else {

            //nullify the asyc task
            mDownloadFormListTask = null;
            Log.e(TAG, " There are no forms to be downloaded ");

            //report an error to formsDownloadingComplete
            FormDetails dummyForm = new FormDetails("There are no forms to be downloaded");
            HashMap<FormDetails, String> dummyHash = new HashMap<>();
            dummyHash.put(dummyForm, "dlerrormessage");
            formsDownloadingComplete(dummyHash);
        }
    }
}
