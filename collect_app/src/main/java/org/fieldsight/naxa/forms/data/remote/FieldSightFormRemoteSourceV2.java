package org.fieldsight.naxa.forms.data.remote;

import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;

import org.fieldsight.naxa.common.GSONInstance;
import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSource;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetails;
import org.fieldsight.naxa.v3.forms.FieldSightFormDownloader;
import org.fieldsight.naxa.v3.network.ApiV3Interface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.dao.FormsDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class FieldSightFormRemoteSourceV2 {

    private static FieldSightFormRemoteSourceV2 INSTANCE;
    private FormsDao formsDao;

    public static FieldSightFormRemoteSourceV2 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormRemoteSourceV2();
        }
        return INSTANCE;
    }

    private FieldSightFormRemoteSourceV2() {
        this.formsDao = new FormsDao();
    }

    private String buildUrlWithParams(List<Project> projects) {
        StringBuilder url = new StringBuilder();
        url.append(APIEndpoint.V3.GET_FORMS);
        url.append("?");
        for (int i = 0; i < projects.size(); i++) {
            url.append(APIEndpoint.PARAMS.PROJECT_ID);
            url.append("=");
            url.append(projects.get(i).getId());
            if (i < projects.size() - 1) {
                url.append("&");
            }
        }
        return url.toString();
    }

    public Observable<Pair<FieldSightFormDetails, String>> getFormUsingProjectId(List<Project> projects) {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getFormsFromUrlAsRaw(buildUrlWithParams(projects))
                .map(this::mapToFieldSightFormDetails)
                .doOnNext(new Consumer<ArrayList<FieldSightFormDetails>>() {
                    @Override
                    public void accept(ArrayList<FieldSightFormDetails> fieldSightFormDetails) {

                        FieldSightFormsLocalSource.getInstance().save(fieldSightFormDetails);
                    }
                })
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

    private ArrayList<FieldSightFormDetails> mapToFieldSightFormDetails(ResponseBody responseBody) throws IOException, JSONException {
        JSONObject response = new JSONObject(responseBody.string());
        Iterator<String> formTypes = response.keys();
        FieldSightFormDetails
                fieldSightForm;
        ArrayList<FieldSightFormDetails> fieldSightForms = new ArrayList<>();
        SparseIntArray projectFormMap = new SparseIntArray();

        while (formTypes.hasNext()) {
            String formKey = formTypes.next();
            JSONArray formList = response.getJSONArray(formKey);

            for (int i = 0; i < formList.length(); i++) {
                JSONObject form = formList.getJSONObject(i);
                fieldSightForm = GSONInstance.getInstance()
                        .fromJson(form.toString(), FieldSightFormDetails.class);
                fieldSightForm.setFormName(fieldSightForm.getOdkFormName());
                fieldSightForm.setFormType(formKey);

                if (TextUtils.equals("stage", formKey)) {
                    //todo: stopping stage download form tests; needs to be removed - Nishon
                    continue;
                }

                fieldSightForm.setProjectId(getProjectId(fieldSightForm));
                fieldSightForms.add(fieldSightForm);
                incrementFormCountForProject(projectFormMap, getProjectId(fieldSightForm));
            }
        }


        for (FieldSightFormDetails fd : fieldSightForms) {
            fd.setTotalFormsInProject(projectFormMap.get(fd.getProjectId()));
        }

        return fieldSightForms;
    }

    private Integer getProjectId(FieldSightFormDetails fieldSightForm) {
        String value = fieldSightForm.getFormDeployedProjectId() != null ? fieldSightForm.getFormDeployedProjectId() : String.valueOf(fieldSightForm.getProjectId());
        return Integer.parseInt(value);
    }


    private Observable<Pair<FieldSightFormDetails, String>> downloadSingleForm(FieldSightFormDetails formDetails, FieldSightFormDownloader formDownloader) {
        return Observable.fromCallable(new Callable<Pair<FieldSightFormDetails, String>>() {
            @Override
            public Pair<FieldSightFormDetails, String> call() throws Exception {
                return formDownloader.downloadSingleFieldSightForm(formDetails);
            }
        });
    }




    private void incrementFormCountForProject(SparseIntArray projectTotalFormMap, Integer projectId) {
        projectTotalFormMap.put(projectId, projectTotalFormMap.get(projectId, 0) + 1);
    }
}
