package org.bcss.collect.naxa.common;

import android.os.Handler;
import android.util.Pair;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.listeners.FormDownloaderListener;
import org.bcss.collect.android.logic.FormDetails;
import org.bcss.collect.naxa.common.exception.DownloadRunningException;
import org.bcss.collect.naxa.common.utilities.FieldSightFormListDownloadUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.onboarding.DownloadProgress;
import org.bcss.collect.naxa.onboarding.SyncableItem;
import org.bcss.collect.naxa.onboarding.XMLForm;
import org.bcss.collect.naxa.onboarding.XMLFormBuilder;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadReceiver;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadService;
import org.bcss.collect.naxa.sync.DownloadableItemLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;
import org.odk.collect.android.utilities.FormDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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


    @Deprecated
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
                        DownloadableItemLocalSource.getINSTANCE().updateProgress(Constant.DownloadUID.ALL_FORMS, progress.getTotal(), progress.getProgress());
                        break;
                    case DownloadProgress.STATUS_ERROR:
                        emitter.onError(new RuntimeException(resultData.getString(Constant.EXTRA_MESSAGE)));
                        break;
                    case DownloadProgress.STATUS_FINISHED_FORM:
                        emitter.onComplete();
                        break;
                }
            });

            XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);
        });
    }

    public Observable<Pair> getFormListByProject(Project project) {
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(project);

        return Observable.just(projects)
                .subscribeOn(Schedulers.io())
                .map(mapProjectsToXMLForm())
                .flatMapIterable((Function<ArrayList<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap(new Function<XMLForm, ObservableSource<Pair>>() {
                    @Override
                    public ObservableSource<Pair> apply(XMLForm xmlForm) throws Exception {
                        return downloadFormlist(xmlForm)
                                .map(new Function<HashMap<String, FormDetails>, Pair>() {
                                    @Override
                                    public Pair apply(HashMap<String, FormDetails> stringFormDetailsHashMap) throws Exception {
                                        return Pair.create(
                                                new ArrayList<>(stringFormDetailsHashMap.keySet())
//                                                        .toArray(
//                                                                new String[stringFormDetailsHashMap.size()]
//                                                        )
                                                , xmlForm.getDownloadUrl());
                                    }
                                });
                    }
                });

    }

    public Observable<List<ArrayList<FormDetails>>> getByProjectId(Project project) {
        return getByProjectId(project, new ArrayList<>(0));
    }

    public Observable<List<ArrayList<FormDetails>>> getByProjectId(Project project, List<String> formsToDownload) {

        ArrayList<Project> projects = new ArrayList<>();
        projects.add(project);


        return Observable.just(projects)
                .subscribeOn(Schedulers.io())
                .map(mapProjectsToXMLForm())
                .flatMapIterable((Function<ArrayList<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap((Function<XMLForm, ObservableSource<HashMap<String, FormDetails>>>) this::downloadFormlist)
                .map((Function<HashMap<String, FormDetails>, HashMap<String, FormDetails>>) hashMap -> {
                    HashMap<String, FormDetails> result = new HashMap<>();

                    for (String key : hashMap.keySet()) {
                        boolean isMatch = formsToDownload.contains(hashMap.get(key).getDownloadUrl());
                        if (formsToDownload.size() == 0) {
                            result.put(key, hashMap.get(key));
                        } else if (isMatch) {
                            result.put(key, hashMap.get(key));
                        }

                    }

                    return result;
                })
                .flatMap(hashMap -> formListDownloadingComplete(hashMap))
                .flatMap((Function<ArrayList<FormDetails>, Observable<ArrayList<FormDetails>>>) this::downloadBulkForms)
                .toList()
                .toObservable();


    }


    private Observable<ArrayList<FormDetails>> formListDownloadingComplete(HashMap<String, FormDetails> formNamesAndURLs) {
        return Observable.fromCallable(new Callable<ArrayList<FormDetails>>() {
            @Override
            public ArrayList<FormDetails> call() throws Exception {
                return cleanDownloadedFormList(formNamesAndURLs);
            }
        });
    }

    @SafeVarargs
    private final Observable<ArrayList<FormDetails>> downloadBulkForms(ArrayList<FormDetails>... values) {


        ArrayList<FormDetails> failedForms = new ArrayList<>();

        return Observable.fromCallable(new Callable<ArrayList<FormDetails>>() {
            @Override
            public ArrayList<FormDetails> call() throws Exception {
                FormDownloader formDownloader = new FormDownloader(false);
                HashMap<FormDetails, String> result = formDownloader.downloadForms(values[0]);
                for (FormDetails key : result.keySet()) {
                    String value = result.get(key);
                    boolean isDownloadSuccessfully = Collect.getInstance().getString(R.string.success).equals(value);
                    if (isDownloadSuccessfully) {
                        failedForms.add(key);
                    }
                }

                return failedForms;
            }
        });

    }


    @SafeVarargs
    private final Observable<HashMap<FormDetails, String>> downloadSingleFormv2(ArrayList<FormDetails>... values) {
        return Observable.fromCallable(new Callable<HashMap<FormDetails, String>>() {
            @Override
            public HashMap<FormDetails, String> call() throws Exception {

                FormDownloader formDownloader = new FormDownloader(false);
                formDownloader.setDownloaderListener(new FormDownloaderListener() {
                    @Override
                    public void progressUpdate(String currentFile, String progress, String total) {
                        Timber.i("%s %s %s", currentFile, progress, total);
                    }

                    @Override
                    public boolean isTaskCanceled() {
                        return false;
                    }
                });

                return formDownloader.downloadForms(values[0]);
            }
        });
    }


    private ArrayList<FormDetails> cleanDownloadedFormList(HashMap<String, FormDetails> result) {

        HashMap<String, FormDetails> formNamesAndURLs;


        if (result == null) {
            Timber.e("Formlist Downloading returned null.  That shouldn't happen");
            throw new RuntimeException(Collect.getInstance().getString(R.string.load_remote_form_error));
        }

        if (result.containsKey(DL_AUTH_REQUIRED)) {

            throw new RuntimeException(Collect.getInstance().getString(R.string.server_requires_auth));

        } else if (result.containsKey(DL_ERROR_MSG)) {
            // Download failed
            throw new RuntimeException(Collect.getInstance().getString(R.string.list_failed_with_error,
                    result.get(DL_ERROR_MSG).getErrorStr()));
        } else {
            // Everything worked. Clear the list and add the results.
            formNamesAndURLs = result;


            //array list added here siteName on Create
            ArrayList<HashMap<String, String>> mFormList = new ArrayList<HashMap<String, String>>();
            ArrayList<FormDetails> filesToDownload = new ArrayList<FormDetails>();


            ArrayList<String> ids = new ArrayList<String>(formNamesAndURLs.keySet());
            for (int i = 0; i < result.size(); i++) {
                String formDetailsKey = ids.get(i);
                FormDetails details = formNamesAndURLs.get(formDetailsKey);
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(FORMNAME, details.getFormName());
                item.put(FORMID_DISPLAY,
                        ((details.getFormVersion() == null) ? "" : (Collect.getInstance().getString(R.string.version) + " " + details.getFormVersion() + " ")) +
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
                        if (name.compareTo(formNamesAndURLs.get(ids.get(i)).getFormName()) > 0) {
                            break;
                        }
                    }
                    mFormList.add(j, item);
                }
            }


            for (int i = 0; i < mFormList.size(); i++) {

                HashMap<String, String> item =
                        (HashMap<String, String>) mFormList.get(i);
                filesToDownload.add(formNamesAndURLs.get(item.get(FORMDETAIL_KEY)));

            }


            return filesToDownload;


        }

    }


    private Function<HashMap<String, FormDetails>, HashMap<String, FormDetails>> checkAndThrowDownloadError() {
        return result -> {

            if (result.containsKey(DL_AUTH_REQUIRED)) {
                throw new RuntimeException("Bad token");
            } else if (result.containsKey(DL_ERROR_MSG)) {
                //todo: give better reason why it failed
                throw new RuntimeException("Download failed");
            }

            return result;
        };


    }

    private Observable<HashMap<String, FormDetails>> downloadFormlist(XMLForm xmlForm) {
        Timber.i("Downloading odk forms from %s", xmlForm.getDownloadUrl());
        return Observable.fromCallable(() -> new FieldSightFormListDownloadUtils().downloadFormList(xmlForm, false))
                .doOnNext(new Consumer<HashMap<String, FormDetails>>() {
                    @Override
                    public void accept(HashMap<String, FormDetails> result) throws Exception {
                        if (result.containsKey(DL_AUTH_REQUIRED)) {
                            throw new RuntimeException("Bad token");
                        } else if (result.containsKey(DL_ERROR_MSG)) {
                            //todo: give better reason why it failed
                            throw new RuntimeException("Download failed");
                        }
                    }
                });
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
                    if (syncableItem.isProgressStatus()) {
                        throw new DownloadRunningException("Waiting until project and sites are downloaded");
                    }
                    if (syncableItem.getDownloadingStatus() == Constant.DownloadStatus.PENDING) {
//                        throw new FormDownloadFailedException("Download project sites first");
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


    public Observable<ArrayList<FormDetails>> getFormsUsingProjectId(Project project) {


        ArrayList<Project> projects = new ArrayList<>();
        projects.add(project);

        return Observable.just(projects)
                .subscribeOn(Schedulers.io())
                .map(mapProjectsToXMLForm())
                .flatMapIterable((Function<ArrayList<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap((Function<XMLForm, ObservableSource<HashMap<FormDetails, String>>>) this::getFormDownloadObservable)
                .map(new Function<HashMap<FormDetails, String>, ArrayList<FormDetails>>() {
                    @Override
                    public ArrayList<FormDetails> apply(HashMap<FormDetails, String> formDetailsStringHashMap) throws Exception {

                        ArrayList<FormDetails> failedForms = new ArrayList<>();
                        for (FormDetails key : formDetailsStringHashMap.keySet()) {
                            String value = formDetailsStringHashMap.get(key);
                            boolean isDownloadSuccessfully = Collect.getInstance().getString(R.string.success).equals(value);
                            if (isDownloadSuccessfully) {
                                failedForms.add(key);
                            }
                        }

                        return failedForms;
                    }
                });
    }


    private Observable<HashMap<FormDetails, String>> getFormDownloadObservable(XMLForm xmlform) {

        return Observable.fromCallable(() -> {
            HashMap<String, FormDetails> formDetailsHashMap = new FieldSightFormListDownloadUtils().downloadFormList(xmlform, false);
            ArrayList<FormDetails> formDetailsArrayList = new ArrayList<>();
            for (String key : formDetailsHashMap.keySet()) {
                formDetailsArrayList.add(formDetailsHashMap.get(key));
            }

            FormDownloader formDownloader = new FormDownloader(false);
            return formDownloader.downloadForms(formDetailsArrayList);
        });
    }
}
