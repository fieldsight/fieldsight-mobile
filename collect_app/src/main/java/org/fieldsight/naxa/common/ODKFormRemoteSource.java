package org.fieldsight.naxa.common;

import android.os.Handler;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormDetails;
import org.fieldsight.naxa.common.utilities.FieldSightFormListDownloadUtils;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.onboarding.DownloadProgress;
import org.fieldsight.naxa.onboarding.XMLForm;
import org.fieldsight.naxa.onboarding.XMLFormBuilder;
import org.fieldsight.naxa.onboarding.XMLFormDownloadReceiver;
import org.fieldsight.naxa.onboarding.XMLFormDownloadService;
import org.fieldsight.naxa.sync.DownloadableItemLocalSource;
import org.odk.collect.android.utilities.FormDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class ODKFormRemoteSource {


    private final static ODKFormRemoteSource INSTANCE = new ODKFormRemoteSource();

    public static ODKFormRemoteSource getInstance() {
        return INSTANCE;
    }


    public Observable<ArrayList<FormDetails>> getFormsUsingProjectId(Project project) {
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(project);

        return Observable.just(projects)
                .subscribeOn(Schedulers.io())
                .map(mapProjectsToXMLForm())
                .flatMap(new Function<ArrayList<XMLForm>, Observable<HashMap<FormDetails, String>>>() {
                    @Override
                    public Observable<HashMap<FormDetails, String>> apply(ArrayList<XMLForm> xmlForms) {
                        return createFormDownloadObservable(xmlForms);
                    }
                })
                .map(new Function<HashMap<FormDetails, String>, ArrayList<FormDetails>>() {
                    @Override
                    public ArrayList<FormDetails> apply(HashMap<FormDetails, String> formDetailsStringHashMap) {

                        ArrayList<FormDetails> failedForms = new ArrayList<>();
                        for (FormDetails key : formDetailsStringHashMap.keySet()) {
                            String value = formDetailsStringHashMap.get(key);
                            boolean hasDownloadFailed = !Collect.getInstance().getString(R.string.success).equals(value);
                            if(hasDownloadFailed){
                                if(key.getFormID() != null){
                                    failedForms.add(key);
                                }
                            }
                        }

                        Timber.i("%d forms failed to download",failedForms.size());
                        return failedForms;
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


    private Observable<HashMap<FormDetails, String>> createFormDownloadObservable(ArrayList<XMLForm> xmlForms) {

        return Observable.fromCallable(() -> {

            HashMap<String, FormDetails> formDetailsHashMap = new HashMap<>();
            for (XMLForm xmlForm : xmlForms) {
                Timber.i("Getting form list from %s", xmlForm.getDownloadUrl());
                formDetailsHashMap.putAll(new FieldSightFormListDownloadUtils().downloadFormList(xmlForm, false));
            }

            ArrayList<FormDetails> formDetailsArrayList = new ArrayList<>();
            for (String key : formDetailsHashMap.keySet()) {
                formDetailsArrayList.add(formDetailsHashMap.get(key));
            }

            FormDownloader formDownloader = new FormDownloader(false);
            HashMap<FormDetails, String> forms = formDownloader.downloadForms(formDetailsArrayList);

            Timber.i("Downloaded %s forms from %s and %s",forms.size(),xmlForms.get(0).getDownloadUrl(),xmlForms.get(1).getDownloadUrl());
            return forms;
        });
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
}
