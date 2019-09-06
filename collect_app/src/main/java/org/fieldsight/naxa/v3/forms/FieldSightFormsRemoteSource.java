package org.fieldsight.naxa.v3.forms;

import android.util.Pair;
import android.util.SparseIntArray;

import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.generalforms.data.GeneralFormLocalSource;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.fieldsight.naxa.scheduled.data.ScheduledFormsLocalSource;
import org.fieldsight.naxa.survey.SurveyForm;
import org.fieldsight.naxa.survey.SurveyFormLocalSource;
import org.fieldsight.naxa.v3.network.ApiV3Interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class FieldSightFormsRemoteSource {

    private static FieldSightFormsRemoteSource INSTANCE;

    public static FieldSightFormsRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormsRemoteSource();
        }
        return INSTANCE;
    }

    private String buildUrlParams(ArrayList<Project> projects) {
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


    private void putOrUpdate(SparseIntArray projectFormMap, Integer projectId) {
        projectFormMap.put(projectId, projectFormMap.get(projectId, 0) + 1);
    }

    private int getProjectId(GeneralForm generalForm) {
        String value = generalForm.getProjectId() != null ? generalForm.getProjectId() : generalForm.getSiteProjectId();
        return Integer.parseInt(value);
    }

    private int getProjectId(ScheduleForm scheduleForm) {
        String value = scheduleForm.getProjectId() != null ? scheduleForm.getProjectId() : scheduleForm.getSiteProjectId();
        return Integer.parseInt(value);
    }

    private int getProjectId(SurveyForm surveyForm) {
        String value = surveyForm.getProjectId() != null ? surveyForm.getProjectId() : surveyForm.getSiteProjectId();
        return Integer.parseInt(value);
    }

    public Observable<Pair<FieldSightFormDetails, String>> getFormsByProjectId(ArrayList<Project> projects) {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getForms(buildUrlParams(projects))
                .map(this::getFormDetails)
                .flatMap((Function<ArrayList<FieldSightFormDetails>, ObservableSource<Pair<FieldSightFormDetails, String>>>) fieldSightFormDetails -> {
                    FieldSightFormDownloader fieldSightFormDownloader = new FieldSightFormDownloader(false);

                    return Observable.just(fieldSightFormDetails)
                            .flatMapIterable((Function<ArrayList<FieldSightFormDetails>, Iterable<FieldSightFormDetails>>) fieldSightFormDetails1 -> fieldSightFormDetails1)
                            .concatMap((Function<FieldSightFormDetails, ObservableSource<Pair<FieldSightFormDetails, String>>>) formDetails -> downloadSingleForm(formDetails, fieldSightFormDownloader)).doOnNext(new Consumer<Pair<FieldSightFormDetails, String>>() {
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
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Saves forms and then generates FormDetails
     *
     * @param fieldSightFormResponse a wrapper for general, schedule, survey and staged forms
     * @return FormDetails: which contains details about each xml form
     */
    private ArrayList<FieldSightFormDetails> getFormDetails(FieldSightFormResponse fieldSightFormResponse) {
        HashSet<FieldSightFormDetails> formListSet = new HashSet<>();
        SparseIntArray projectFormMap = new SparseIntArray();

        for (GeneralForm generalForm : fieldSightFormResponse.getGeneralForms()) {
            String formName = generalForm.getName();
            String downloadUrl = APIEndpoint.BASE_URL.concat(generalForm.getDownloadUrl());
            String manifestUrl = APIEndpoint.BASE_URL.concat(generalForm.getManifestUrl());
            String formId = generalForm.getFsFormId();
            String hash = generalForm.getHash();
            String version = generalForm.getVersion();

            String deployedFrom = generalForm.getProjectId() != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
            generalForm.setFormDeployedFrom(deployedFrom);

            formListSet.add(new FieldSightFormDetails(getProjectId(generalForm), formName, downloadUrl, manifestUrl, formId,
                    version, hash, null,
                    false, false));

            putOrUpdate(projectFormMap, getProjectId(generalForm));

        }

        GeneralFormLocalSource.getInstance().save(fieldSightFormResponse.getGeneralForms());

        for (ScheduleForm scheduleForm : fieldSightFormResponse.getScheduleForms()) {
            String formName = scheduleForm.getFormName();
            String downloadUrl = APIEndpoint.BASE_URL.concat(scheduleForm.getDownloadUrl());
            String manifestUrl = APIEndpoint.BASE_URL.concat(scheduleForm.getManifestUrl());
            String formId = scheduleForm.getFsFormId();
            String hash = scheduleForm.getHash();
            String version = scheduleForm.getVersion();

            String deployedFrom = scheduleForm.getProjectId() != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
            scheduleForm.setFormDeployedFrom(deployedFrom);

            formListSet.add(new FieldSightFormDetails(getProjectId(scheduleForm), formName, downloadUrl, manifestUrl, formId,
                    version, hash, null,
                    false, false));

            putOrUpdate(projectFormMap, getProjectId(scheduleForm));
        }

        ScheduledFormsLocalSource.getInstance().save(fieldSightFormResponse.getScheduleForms());

        for (SurveyForm surveyForm : fieldSightFormResponse.getSurveyForms()) {
            String formName = surveyForm.getName();
            String downloadUrl = APIEndpoint.BASE_URL.concat(surveyForm.getDownloadUrl());
            String manifestUrl = APIEndpoint.BASE_URL.concat(surveyForm.getManifestUrl());
            String formId = surveyForm.getFsFormId();
            String hash = surveyForm.getHash();
            String version = surveyForm.getVersion();


            formListSet.add(new FieldSightFormDetails(getProjectId(surveyForm), formName, downloadUrl, manifestUrl, formId,
                    version, hash, null,
                    false, false));

            putOrUpdate(projectFormMap, getProjectId(surveyForm));
        }

        SurveyFormLocalSource.getInstance().save(fieldSightFormResponse.getSurveyForms());
//                    for (Stage stage : fieldSightFormResponse.getStages()) {
//                        for (SubStage subStage : stage.getSubStage()) {
//                            String formName = subStage.getName();
//                            String downloadUrl = APIEndpoint.BASE_URL.concat(subStage.getStageForms().getDownloadUrl());
//                            String manifestUrl = APIEndpoint.BASE_URL.concat(subStage.getStageForms().getManifestUrl());
//                            String hash = subStage.getStageForms().getHash();
//                            String formId = subStage.getStageForms().getId();
//                            String version = subStage.getStageForms().getVersion();
//
//                            formList.add(new FormDetails(formName, downloadUrl, manifestUrl, formId,
//                                    version, hash, null,
//                                    false, false));
//                        }
//
//                    }

        ArrayList<FieldSightFormDetails> formList = new ArrayList<>(formListSet);
        for (FieldSightFormDetails fd : formList) {
            fd.setTotalFormsInProject(projectFormMap.get(fd.getProjectId()));
        }

        return formList;
    }

    private Observable<HashMap<FieldSightFormDetails, String>> createFormDownloadObservable(ArrayList<FieldSightFormDetails> formDetailsArrayList) {
        return Observable.fromCallable(() -> {

            FieldSightFormDownloader formDownloader = new FieldSightFormDownloader(false);
            return formDownloader.downloadFieldSightForms(formDetailsArrayList);
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
}
