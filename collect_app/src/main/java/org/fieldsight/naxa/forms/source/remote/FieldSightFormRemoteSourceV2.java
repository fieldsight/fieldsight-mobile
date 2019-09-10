package org.fieldsight.naxa.forms.source.remote;

import android.util.Pair;
import android.util.SparseIntArray;

import com.google.android.gms.common.util.ArrayUtils;

import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.forms.source.local.FieldSightForm;
import org.fieldsight.naxa.forms.source.local.FieldSightFormsLocalSource;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.v3.forms.FieldSightFormDetails;
import org.fieldsight.naxa.v3.forms.FieldSightFormDownloader;
import org.fieldsight.naxa.v3.network.ApiV3Interface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class FieldSightFormRemoteSourceV2 {

    private static FieldSightFormRemoteSourceV2 INSTANCE;

    public static FieldSightFormRemoteSourceV2 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormRemoteSourceV2();
        }
        return INSTANCE;
    }

    private String buildUrlWithParams(List<Project> projects) {
        StringBuilder url = new StringBuilder();
        url.append(APIEndpoint.V3.GET_FORMS);
        url.append("?");
        for (int i = 0; i < projects.size(); i++) {
            url.append(APIEndpoint.PARAMS.PROJECT_ID);
            url.append("=");
            url.append(projects.get(i).getId());
            if (i != projects.size() - 1) {
                url.append("&");
            }
        }
        return url.toString();
    }

    public Observable<Pair<FieldSightFormDetails, String>> getFormFromProjectId(List<Project> projects) {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getFormsFromUrl(buildUrlWithParams(projects))
                .map(this::getFormDetails)
                .flatMap((Function<ArrayList<FieldSightFormDetails>, ObservableSource<Pair<FieldSightFormDetails, String>>>) fieldSightFormDetails -> {
                    FieldSightFormDownloader fieldSightFormDownloader = new FieldSightFormDownloader(false);


                    return Observable.just(fieldSightFormDetails)
                            .flatMapIterable((Function<ArrayList<FieldSightFormDetails>, Iterable<FieldSightFormDetails>>) fieldSightFormDetails1 -> fieldSightFormDetails1)
                            .concatMap((Function<FieldSightFormDetails, ObservableSource<Pair<FieldSightFormDetails, String>>>) formDetails -> downloadSingleForm(formDetails, fieldSightFormDownloader))
                            .doOnNext(new Consumer<Pair<FieldSightFormDetails, String>>() {
                                @Override
                                public void accept(Pair<FieldSightFormDetails, String> fieldSightFormDetailsStringPair) {
                                    if (fieldSightFormDetailsStringPair == null) {
                                        Timber.w("FieldSightFormDetails pair is null");
                                        return;
                                    }

                                    String message = fieldSightFormDetailsStringPair.second;
                                    Timber.i(message);
                                }
                            });
                });
    }

    private Observable<Pair<FieldSightFormDetails, String>> downloadSingleForm(FieldSightFormDetails formDetails, FieldSightFormDownloader formDownloader) {
        return Observable.fromCallable(new Callable<Pair<FieldSightFormDetails, String>>() {
            @Override
            public Pair<FieldSightFormDetails, String> call() throws Exception {
                return formDownloader.downloadSingleFieldSightForm(formDetails);
            }
        });
    }

    private ArrayList<FieldSightFormDetails> getFormDetails(FieldSightFormsResponse fieldSightFormsResponse) {
        HashSet<FieldSightFormDetails> formListSet = new HashSet<>();
        SparseIntArray projectFormMap = new SparseIntArray();
        Timber.i("getFormDetails %d", formListSet.size());
        for (FieldSightForm fieldSightForm : fieldSightFormsResponse.getGeneralForms()) {
            fieldSightForm.setFormType(Constant.FormType.GENERAl);
            addToDownloadList(fieldSightForm, formListSet, projectFormMap);
        }

        for (FieldSightForm fieldSightForm : fieldSightFormsResponse.getScheduleForms()) {
            fieldSightForm.setFormType(Constant.FormType.SCHEDULE);
            addToDownloadList(fieldSightForm, formListSet, projectFormMap);
        }

        for (FieldSightForm fieldSightForm : fieldSightFormsResponse.getSurveyForms()) {
            fieldSightForm.setFormType(Constant.FormType.SURVEY);
            addToDownloadList(fieldSightForm, formListSet, projectFormMap);
        }

//        for (FieldSightForm fieldSightForm : fieldSightFormsResponse.getStages()) {
//            fieldSightForm.setFormType(Constant.FormType.STAGED);
//            addToDownloadList(fieldSightForm, formListSet, projectFormMap);
//        }

        ArrayList<FieldSightForm> formsToSave = new ArrayList<>();
        formsToSave.addAll(fieldSightFormsResponse.getGeneralForms());
        formsToSave.addAll(fieldSightFormsResponse.getScheduleForms());
        formsToSave.addAll(fieldSightFormsResponse.getStages());
        formsToSave.addAll(fieldSightFormsResponse.getSurveyForms());
        FieldSightFormsLocalSource.getInstance().save(formsToSave);

        return new ArrayList<>(formListSet);
    }

    private void addToDownloadList(FieldSightForm fieldSightForm, HashSet<FieldSightFormDetails> formListSet, SparseIntArray projectFormMap) {
        String formName = fieldSightForm.getOdkFormName();
        String downloadUrl = APIEndpoint.BASE_URL + fieldSightForm.getFormDownloadUrl();
        String manifestUrl = APIEndpoint.BASE_URL + fieldSightForm.getManifestDownloadUrl();
        String formId = fieldSightForm.getOdkFormID();
        String hash = fieldSightForm.getOdkFormHash();
        String version = fieldSightForm.getOdkFormVersion();

        formListSet.add(new FieldSightFormDetails(getProjectId(fieldSightForm), formName, downloadUrl, manifestUrl, formId,
                version, hash, null,
                false, false));
        incrementFormCountForProject(projectFormMap, getProjectId(fieldSightForm));
    }

    private int getProjectId(FieldSightForm fieldSightForm) {
        String value = fieldSightForm.getFormDeployedProjectId() != null ? fieldSightForm.getFormDeployedProjectId() : fieldSightForm.getFormDeployedSiteId();
        return Integer.parseInt(value);
    }


    private void incrementFormCountForProject(SparseIntArray projectFormMap, Integer projectId) {
        projectFormMap.put(projectId, projectFormMap.get(projectId, 0) + 1);
    }
}
