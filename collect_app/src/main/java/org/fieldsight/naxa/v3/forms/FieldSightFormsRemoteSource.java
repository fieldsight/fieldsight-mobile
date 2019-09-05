package org.fieldsight.naxa.v3.forms;

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
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.utilities.FormDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

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

    /**
     * Option:
     * project_id -->
     *
     *
     *
     *
     * @param generalForm
     * @return
     */

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

    public Observable<Object> getFormsByProjectId(ArrayList<Project> projects) {


        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getForms(buildUrlParams(projects))
                .map(fieldSightFormResponse -> {
                    HashSet<FormDetails> formList = new HashSet<>();
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

                        formList.add(new FormDetails(formName, downloadUrl, manifestUrl, formId,
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

                        formList.add(new FormDetails(formName, downloadUrl, manifestUrl, formId,
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

                        formList.add(new FormDetails(formName, downloadUrl, manifestUrl, formId,
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

                    return new ArrayList<>(formList);
                })
                .switchMap(new Function<ArrayList<FormDetails>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(ArrayList<FormDetails> formDetails) throws Exception {
                        return createFormDownloadObservable(formDetails);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());


    }


    private Observable<HashMap<FormDetails, String>> createFormDownloadObservable(ArrayList<FormDetails> formDetailsArrayList) {

        return Observable.fromCallable(() -> {


            FormDownloader formDownloader = new FormDownloader(false);
            HashMap<FormDetails, String> forms = formDownloader.downloadForms(formDetailsArrayList);

            return forms;
        });
    }
}
