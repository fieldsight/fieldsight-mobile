package org.bcss.collect.naxa.common;

import android.os.Handler;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.listeners.FormDownloaderListener;
import org.bcss.collect.android.logic.FormDetails;
import org.bcss.collect.naxa.common.exception.DownloadRunningException;
import org.bcss.collect.naxa.common.exception.FormDownloadFailedException;
import org.bcss.collect.naxa.common.utilities.FieldSightFormListDownloadUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.onboarding.DownloadProgress;
import org.bcss.collect.naxa.onboarding.SyncableItem;
import org.bcss.collect.naxa.onboarding.XMLForm;
import org.bcss.collect.naxa.onboarding.XMLFormBuilder;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadReceiver;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadService;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.sync.DisposableManager;
import org.bcss.collect.naxa.sync.SyncLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;
import org.odk.collect.android.utilities.FormDownloader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.ODK_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.PROJECT_SITES;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.android.activities.FormDownloadList.FORMDETAIL_KEY;
import static org.odk.collect.android.activities.FormDownloadList.FORMID_DISPLAY;
import static org.odk.collect.android.activities.FormDownloadList.FORMNAME;
import static org.odk.collect.android.activities.FormDownloadList.FORM_ID_KEY;
import static org.odk.collect.android.activities.FormDownloadList.FORM_VERSION_KEY;
import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_AUTH_REQUIRED;
import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_ERROR_MSG;

public class ODKFormRemoteSource {


    private final static ODKFormRemoteSource INSTANCE = new ODKFormRemoteSource();

    public static ODKFormRemoteSource getInstance() {
        return INSTANCE;
    }

    public Observable<DownloadProgress> fetchODKForms() {
        int uid = Constant.DownloadUID.ODK_FORMS;
        return Observable.create(emitter -> {
            XMLFormDownloadReceiver xmlFormDownloadReceiver = new XMLFormDownloadReceiver(new Handler());

            xmlFormDownloadReceiver.setReceiver((resultCode, resultData) -> {
                switch (resultCode) {
                    case DownloadProgress.STATUS_RUNNING:
                        break;
                    case DownloadProgress.STATUS_PROGRESS_UPDATE:
                        DownloadProgress progress = (DownloadProgress) resultData.getSerializable(EXTRA_OBJECT);
                        emitter.onNext(progress);
                        SyncLocalSource.getINSTANCE().updateProgress(Constant.DownloadUID.ALL_FORMS, progress.getTotal(), progress.getProgress());
                        break;
                    case DownloadProgress.STATUS_ERROR:
                        emitter.onError(new RuntimeException("An error occurred while downloading forms"));
                        break;
                    case DownloadProgress.STATUS_FINISHED_FORM:
                        emitter.onComplete();
                        break;
                }
            });

            XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);
        });
    }


    private void updateProgress(String message, int current, int total) {
        Timber.i("%s %d %d", message, current, total);
    }


    public void downloadODKForms() {
        SyncLocalSource.getINSTANCE().markAsRunning(ODK_FORMS);

        DisposableObserver<String[]> dis = getXMLForms().subscribeWith(new DisposableObserver<String[]>() {
            @Override
            public void onNext(String[] strings) {
                Timber.d("onNext() %s", Arrays.toString(strings));
                String name = strings[0];
                String current = strings[1];
                String total = strings[2];

                SyncLocalSource.getINSTANCE().markAsRunning(ODK_FORMS, Arrays.toString(strings));

                if (current.equals(total)) {
                    SyncLocalSource.getINSTANCE().markAsCompleted(ODK_FORMS);
                }
            }

            @Override
            public void onError(Throwable e) {
                SyncLocalSource.getINSTANCE().markAsFailed(ODK_FORMS, e.getMessage());
                Timber.e(e);
            }

            @Override
            public void onComplete() {
                Timber.d("onComplete()");
            }
        });

        DisposableManager.add(dis);
    }

    private Observable<String[]> getXMLForms() {
        return checkIfProjectSitesDownloaded()
                .flatMapSingle((Function<SyncableItem, SingleSource<List<Project>>>) syncableItem -> ProjectLocalSource.getInstance().getProjectsMaybe())
                .map(mapProjectsToXMLForm())
                .flatMapIterable((Function<ArrayList<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap((Function<XMLForm, ObservableSource<HashMap<String, FormDetails>>>) this::downloadFormlist)
                .map(checkAndThrowDownloadError())
                .toList()
                .toObservable()
                .map(new Function<List<HashMap<String, FormDetails>>, HashMap<String, FormDetails>>() {
                    @Override
                    public HashMap<String, FormDetails> apply(List<HashMap<String, FormDetails>> hashMaps) throws Exception {
                        HashMap<String, FormDetails> result = new HashMap<>();
                        for (HashMap<String, FormDetails> hashMap : hashMaps) {
                            result.putAll(hashMap);
                        }
                        return result;

                    }
                })
                .flatMap(new Function<HashMap<String, FormDetails>, ObservableSource<ArrayList<FormDetails>>>() {
                    @Override
                    public ObservableSource<ArrayList<FormDetails>> apply(HashMap<String, FormDetails> formNamesAndURLs) throws Exception {
                        return formListDownloadingComplete(formNamesAndURLs);
                    }
                })
                .flatMap(new Function<ArrayList<FormDetails>, ObservableSource<String[]>>() {
                    @Override
                    public ObservableSource<String[]> apply(ArrayList<FormDetails> formDetails) throws Exception {
                        return downloadSingleForm(formDetails);
                    }
                });
    }


    private Observable<ArrayList<FormDetails>> formListDownloadingComplete(HashMap<String, FormDetails> formNamesAndURLs) {
        return Observable.fromCallable(new Callable<ArrayList<FormDetails>>() {
            @Override
            public ArrayList<FormDetails> call() throws Exception {
                return cleanDownloadedFormList(formNamesAndURLs);
            }
        });
    }

    private Observable<String[]> downloadSingleForm(ArrayList<FormDetails>... values) {


        return Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                FormDownloader formDownloader = new FormDownloader(false);
                formDownloader.setDownloaderListener(new FormDownloaderListener() {
                    @Override
                    public void progressUpdate(String currentFile, String progress, String total) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(new String[]{currentFile, progress, total});
                        }

                        if (progress.equals(total)) {
                            emitter.onComplete();
                        }
                    }

                    @Override
                    public boolean isTaskCanceled() {
                        return false;
                    }
                });

                formDownloader.downloadForms(values[0]);
            }
        });

    }

    private ArrayList<FormDetails> cleanDownloadedFormList(HashMap<String, FormDetails> formNamesAndURLs) {
        HashMap<String, FormDetails> result = new HashMap<>();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        result = formNamesAndURLs;
        ArrayList<HashMap<String, String>> filteredFormList = new ArrayList<>();
        String[] formIdsToDownload;
        HashMap<String, Boolean> formResult = new HashMap<>();
        ArrayList<String> formsFound = new ArrayList<>();

        ArrayList<String> ids = new ArrayList<String>(formNamesAndURLs.keySet());
        for (int i = 0; i < result.size(); i++) {
            String formDetailsKey = ids.get(i);
            FormDetails details = formNamesAndURLs.get(formDetailsKey);

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


        filteredFormList.addAll(formList);

        ArrayList<FormDetails> filesToDownload = new ArrayList<>();

        for (FormDetails formDetails : formNamesAndURLs.values()) {
            String formId = formDetails.getFormID();

            formsFound.add(formId);
            filesToDownload.add(formDetails);

        }


        return filesToDownload;

    }


    private Function<HashMap<String, FormDetails>, HashMap<String, FormDetails>> checkAndThrowDownloadError() {
        return result -> {

            if (result.containsKey(DL_AUTH_REQUIRED)) {
                throw new FormDownloadFailedException("Bad token");
            } else if (result.containsKey(DL_ERROR_MSG)) {
                //todo: give better reason why it failed
                throw new FormDownloadFailedException("Download failed");
            }

            return result;
        };


    }

    private Observable<HashMap<String, FormDetails>> downloadFormlist(XMLForm xmlForm) {
        return Observable.fromCallable(() -> new FieldSightFormListDownloadUtils().downloadFormList(xmlForm, false));
    }

    private Function<List<Project>, ArrayList<XMLForm>> mapProjectsToXMLForm() {
        return projects -> {
            XMLForm xmlForm = null;
            String baseurl = FieldSightUserSession.getServerUrl(Collect.getInstance());
            ArrayList<XMLForm> formsToDownload = new ArrayList<>();

            for (Project project : projects) {
                xmlForm = new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(false)
                        .setDownloadUrl(baseurl + APIEndpoint.ASSIGNED_FORM_LIST_SITE.concat(project.getId()))
                        .createXMLForm();
                formsToDownload.add(xmlForm);

                xmlForm = new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .setDownloadUrl(baseurl + APIEndpoint.ASSIGNED_FORM_LIST_PROJECT.concat(project.getId()))
                        .createXMLForm();
                formsToDownload.add(xmlForm);
            }

            return formsToDownload;
        };
    }

    private Observable<SyncableItem> checkIfProjectSitesDownloaded() {
        int timeToWaitInSeconds = 3;
        return SyncRepository.getInstance()
                .getStatusById(PROJECT_SITES)
                .map(syncableItem -> {
//                    if (syncableItem.isProgressStatus()) {
//                        throw new DownloadRunningException("Waiting until project and sites are downloaded");
//                    }
                    if (syncableItem.getDownloadingStatus() != Constant.DownloadStatus.COMPLETED) {
                        throw new DownloadRunningException("Download project sites first");
                    }
                    return syncableItem;
                })
                .toObservable()
                .retryWhen(throwableObservable -> throwableObservable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
                    if (throwable instanceof DownloadRunningException) {
                        Timber.i("Polling for project sites");
                        return Observable.timer(timeToWaitInSeconds, TimeUnit.SECONDS);
                    }
                    return Observable.just(throwable);
                }));
    }


}
