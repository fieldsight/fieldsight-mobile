package org.odk.collect.naxa.onboarding;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.DownloadFormsTaskListener;
import org.odk.collect.android.listeners.FormListDownloaderListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.login.model.MySites;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.project.db.ProjectViewModel;
import org.odk.collect.naxa.site.db.SiteViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.odk.collect.android.activities.FormDownloadList.FORMDETAIL_KEY;
import static org.odk.collect.android.activities.FormDownloadList.FORMID_DISPLAY;
import static org.odk.collect.android.activities.FormDownloadList.FORMNAME;
import static org.odk.collect.android.activities.FormDownloadList.FORM_ID_KEY;
import static org.odk.collect.android.activities.FormDownloadList.FORM_VERSION_KEY;
import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_AUTH_REQUIRED;
import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_ERROR_MSG;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;

public class DownloadModelImpl implements DownloadModel {

    private SiteViewModel siteViewModel;
    private ProjectViewModel projectViewModel;
    private DownloadFormsTask downloadFormsTask;
    private HashMap<String, FormDetails> formNamesAndURLs;
    private HashMap<String, FormDetails> formList;
    private DownloadFormListTask downloadFormListTask;

    DownloadModelImpl() {
        this.siteViewModel = new SiteViewModel(Collect.getInstance());
        this.projectViewModel = new ProjectViewModel(Collect.getInstance());
    }


    @Override
    public void fetchGeneralForms() {

    }

    @Override
    public void fetchProjectSites() {

        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUserInformation()
                .flatMap((Function<MeResponse, ObservableSource<List<MySites>>>) meResponse -> Observable.just(meResponse.getData().getMySitesModel()))
                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(mySites -> {
                    siteViewModel.insert(mySites.getSite());
                    projectViewModel.insert(mySites.getProject());
                    return mySites.getProject();
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsebable(Constant.DownloadUID.PROJECT_SITES));
    }

    @Override
    public void fetchODKForms() {
        formNamesAndURLs = new HashMap<String, FormDetails>();
        if (downloadFormListTask != null && downloadFormListTask.getStatus() != AsyncTask.Status.FINISHED) {
            return; // we are already doing the download!!!
        } else if (downloadFormListTask != null) {
            downloadFormListTask.setDownloaderListener(null);
            downloadFormListTask.cancel(true);
            downloadFormListTask = null;
        }

        downloadFormListTask = new DownloadFormListTask();
        downloadFormListTask.setDownloaderListener(new FormListDownloaderListener() {
            @Override
            public void formListDownloadingComplete(HashMap<String, FormDetails> value) {
                downloadForm(value);
            }
        });
        downloadFormListTask.execute();
    }

    private void downloadForm(HashMap<String, FormDetails> result) {
        downloadFormListTask.setDownloaderListener(null);
        downloadFormListTask = null;

        if (result == null) {
            Timber.e("Formlist Downloading returned null.  That shouldn't happen");
            // Just displayes "error occured" to the user, but this should never happen.
//            createAlertDialog(getString(R.string.load_remote_form_error),
//                    getString(R.string.error_occured), EXIT);
            return;
        }

        if (result.containsKey(DL_AUTH_REQUIRED)) {
            // need authorization
//            showDialog(AUTH_DIALOG);
        } else if (result.containsKey(DL_ERROR_MSG)) {
            // Download failed
            String dialogMessage =
                    Collect.getInstance().getString(R.string.list_failed_with_error,
                            result.get(DL_ERROR_MSG).getErrorStr());
            String dialogTitle = Collect.getInstance().getString(R.string.load_remote_form_error);
//            createAlertDialog(dialogTitle, dialogMessage, DO_NOT_EXIT);
        } else {
            // Everything worked. Clear the list and add the results.
            formNamesAndURLs = result;
            formList.clear();

            ArrayList<String> ids = new ArrayList<String>(formNamesAndURLs.keySet());
            for (int i = 0; i < result.size(); i++) {
                String formDetailsKey = ids.get(i);
                FormDetails details = formNamesAndURLs.get(formDetailsKey);

                boolean displayOnlyUpdatedForms = false;
                if ((details.isNewerFormVersionAvailable() || details.areNewerMediaFilesAvailable())) {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put(FORMNAME, details.getFormName());
                    item.put(FORMID_DISPLAY,
                            ((details.getFormVersion() == null) ? "" : (Collect.getInstance().getString(R.string.version) + " "
                                    + details.getFormVersion() + " ")) + "ID: " + details.getFormID());
                    item.put(FORMDETAIL_KEY, formDetailsKey);
                    item.put(FORM_ID_KEY, details.getFormID());
                    item.put(FORM_VERSION_KEY, details.getFormVersion());

                    // Insert the new form in alphabetical order.
                    if (formList.isEmpty()) {
                        formList.add(item);
                    } else {
                        int j;
                        for (j = 0; j < formList.size(); j++) {
                            HashMap<String, String> compareMe = formList.get(j);
                            String name = compareMe.get(FORMNAME);
                            if (name.compareTo(formNamesAndURLs.get(ids.get(i)).getFormName()) > 0) {
                                break;
                            }
                        }
                        formList.add(j, item);
                    }
                }
            }

            downloadSelectedFiles(formNamesAndURLs);
        }

    }


    private void downloadSelectedFiles(HashMap<String, FormDetails> formNamesAndURLs) {

        int totalCount = 0;
        ArrayList<FormDetails> filesToDownload = new ArrayList<>();
        filesToDownload.addAll(formNamesAndURLs.values());
        totalCount = filesToDownload.size();

        if (totalCount > 0) {
            // show dialog box

            downloadFormsTask = new DownloadFormsTask();
            downloadFormsTask.setDownloaderListener(new DownloadFormsTaskListener() {
                @Override
                public void formsDownloadingComplete(HashMap<FormDetails, String> result) {
                    ToastUtils.showShortToastInMiddle("Forms has been synced");
                }

                @Override
                public void progressUpdate(String currentFile, int progress, int total) {
                    String alertMsg = Collect.getInstance().getString(R.string.fetching_file, currentFile, String.valueOf(progress), String.valueOf(total));
                    Timber.i(alertMsg);
                }

                @Override
                public void formsDownloadingCancelled() {

                }
            });
            downloadFormsTask.execute(filesToDownload);
        } else {
            ToastUtils.showShortToast(R.string.noselect_error);
        }
    }


    private SingleObserver<? super List<Project>> getObsebable(int uid) {

        return new SingleObserver<List<Project>>() {
            @Override
            public void onSubscribe(Disposable d) {
                EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_START));
            }

            @Override
            public void onSuccess(List<Project> projects) {
                EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_END));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_ERROR));
            }
        };
    }
}
