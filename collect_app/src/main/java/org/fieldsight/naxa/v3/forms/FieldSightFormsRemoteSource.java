package org.fieldsight.naxa.v3.forms;

import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.fieldsight.naxa.v3.network.ApiV3Interface;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.utilities.FormDownloader;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FieldSightFormsRemoteSource {

    private static FieldSightFormsRemoteSource INSTANCE;

    public static FieldSightFormsRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormsRemoteSource();
        }
        return INSTANCE;
    }


    public Observable<Object> getFormsByProjectId(ArrayList<Project> projects) {
        HashMap<String, String> params = new HashMap<>();

        for (Project project : projects) {
            params.put(APIEndpoint.PARAMS.PROJECT_ID, project.getId());
        }

        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getForms(params)
                .map(new Function<FieldSightFormResponse, ArrayList<FormDetails>>() {
                    @Override
                    public ArrayList<FormDetails> apply(FieldSightFormResponse fieldSightFormResponse) throws Exception {
                        ArrayList<FormDetails> formList = new ArrayList<>();


                        for (GeneralForm generalForm : fieldSightFormResponse.getGeneralForms()) {
                            String formName = generalForm.getName();
                            String downloadUrl = APIEndpoint.BASE_URL.concat(generalForm.getDownloadUrl());
                            String manifestUrl = APIEndpoint.BASE_URL.concat(generalForm.getManifestUrl());
                            String formId = generalForm.getFsFormId();

                            formList.add(new FormDetails(formName, downloadUrl, manifestUrl, formId,
                                    null, null, null,
                                    false, false));
                        }

                        for (ScheduleForm scheduleForm : fieldSightFormResponse.getScheduleForms()) {
                            String formName = scheduleForm.getFormName();
                            String downloadUrl = APIEndpoint.BASE_URL.concat(scheduleForm.getDownloadUrl());
                            String manifestUrl = APIEndpoint.BASE_URL.concat(scheduleForm.getManifestUrl());
                            String formId = scheduleForm.getFsFormId();

                            formList.add(new FormDetails(formName, downloadUrl, manifestUrl, formId,
                                    null, null, null,
                                    false, false));
                        }


                        return formList;
                    }
                })

                .switchMap(new Function<ArrayList<FormDetails>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(ArrayList<FormDetails> formDetails) throws Exception {
                        return createFormDownloadObservable(formDetails);
                    }
                })
                .subscribeOn(Schedulers.io())
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
